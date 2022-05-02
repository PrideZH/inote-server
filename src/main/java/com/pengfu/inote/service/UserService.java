package com.pengfu.inote.service;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengfu.inote.domain.dto.auth.LoginDTO;
import com.pengfu.inote.domain.dto.auth.RegisterDTO;
import com.pengfu.inote.domain.dto.common.PageDTO;
import com.pengfu.inote.domain.dto.user.UserPatchDTO;
import com.pengfu.inote.domain.entity.User;
import com.pengfu.inote.domain.dto.auth.EmailCodeDTO;
import com.pengfu.inote.domain.vo.common.TokenVO;
import com.pengfu.inote.domain.vo.user.UserInfoVO;
import com.pengfu.inote.domain.vo.user.UserOpenVO;
import com.pengfu.inote.domain.vo.user.UserVO;
import com.pengfu.inote.manager.FileManager;
import com.pengfu.inote.manager.UserManager;
import com.pengfu.inote.mapper.UserMapper;
import com.pengfu.inote.service.exception.ServiceException;
import com.pengfu.inote.utils.RedisUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@AllArgsConstructor
@Service
public class UserService {

    private UserMapper userMapper;

    private UserManager userManager;
    private FileManager fileManager;

    private RedisUtil redisUtil;

    private JavaMailSender mailSender;

    /** 验证码有效期 */
    private final static Long CAPTCHA_EXP = 600L;

    /**
     * 注册
     */
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO registerDTO) {
        User user = userMapper.selectOne(new QueryWrapper<User>().lambda()
                .eq(User::getUsername, registerDTO.getUsername()));

        if (user != null) {
            throw new ServiceException(1001, "用户已存在");
        } else {
            String code = (String) redisUtil.get(registerDTO.getUsername());

            if (!registerDTO.getCode().equals(code)) {
                throw new ServiceException(1002, "验证码错误");
            }

            redisUtil.del(registerDTO.getUsername());

            user = new User();
            BeanUtils.copyProperties(registerDTO, user);
            user.setPassword(SaSecureUtil.md5(registerDTO.getPassword()));
            user.setLoginTime(LocalDateTime.now());
            userMapper.insert(user);

            // 修改默认名为 inote_:id
            user.setNickname("inote_" + user.getId());
            userMapper.updateById(user);
        }
    }

    /**
     * 生成验证码
     */
    @Async
    public void getCode(EmailCodeDTO emailCodeDTO) {
        // 生成验证码
        int code = (int) (Math.random() * 1000000);
        String codeString = String.valueOf(code);
        if (codeString.length() != 6) {
            code = code + 100000;
        }
        redisUtil.set(emailCodeDTO.getEmail(), String.valueOf(code), CAPTCHA_EXP);

        // 发送邮件
        SimpleMailMessage message = new SimpleMailMessage();
        // 设置邮箱标题
        message.setSubject("iNote - 邮箱验证");
        // 设置邮箱内容
        message.setText("您的邮箱验证码为："+ code);
        // 发送者邮箱
        message.setFrom("332842890@qq.com");
        message.setTo(emailCodeDTO.getEmail());
        mailSender.send(message);
    }

    /**
     * 登录
     */
    public TokenVO login(LoginDTO loginDTO) {
        User user = userMapper.selectOne(new QueryWrapper<User>().lambda()
                .eq(User::getUsername, loginDTO.getUsername()));

        if (user == null) {
            throw new ServiceException(1001, "用户不存在");
        }

        if (user.getPassword().equals(SaSecureUtil.md5(loginDTO.getPassword()))) {
            StpUtil.login(user.getId());

            user.setLoginTime(LocalDateTime.now());
            userMapper.updateById(user);

            SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
            return TokenVO.build(tokenInfo);
        } else {
            throw new ServiceException(1002, "密码错误");
        }
    }

    /**
     * 登出
     */
    public void logout() {
        StpUtil.logout();
    }

    public void refreshToken() {
        User user = userManager.getById(StpUtil.getLoginIdAsLong());

        user.setLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        StpUtil.renewTimeout(86400);
    }

    /**
     * 获取所有用户数据
     */
    public IPage<UserVO> getList(PageDTO pageDTO) {
        return userMapper.selectPageVo(new Page<User>(pageDTO.getPage(), pageDTO.getSize()));
    }

    /**
     * 获取个人信息
     */
    public UserInfoVO getMe() {
        Long userId = Long.valueOf(StpUtil.getLoginIdAsString());
        User user = userManager.getById(userId);
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);
        return userInfoVO;
    }

    /**
     * 获取指定用户的公开数据
     */
    public UserOpenVO getOpen(Long id) {
        User user = userManager.getById(id);
        UserOpenVO userOpenVO = new UserOpenVO();
        BeanUtils.copyProperties(user, userOpenVO);
        return userOpenVO;
    }

    public UserInfoVO update(UserPatchDTO userPatchDTO) {
        User user = userManager.getById(userPatchDTO.getId());

        User userValue = new User();
        BeanUtils.copyProperties(userPatchDTO, userValue);
        userMapper.updateById(userValue);

        // 若修改了头像图片 且旧图片不在被引用 则删除旧图片文件
        if (userPatchDTO.getAvatarUrl() != null && user.getAvatarUrl() != null) {
            if (userMapper.selectCount(new QueryWrapper<User>().lambda()
                    .eq(User::getAvatarUrl, user.getAvatarUrl())) == 0) {
                fileManager.delete(user.getAvatarUrl());
            }
        }

        user = userManager.getById(userPatchDTO.getId());
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);
        return userInfoVO;
    }

}
