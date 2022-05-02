package com.pengfu.inote.service;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengfu.inote.domain.dto.auth.LoginDTO;
import com.pengfu.inote.domain.dto.common.PageDTO;
import com.pengfu.inote.domain.entity.Admin;
import com.pengfu.inote.domain.vo.common.TokenVO;
import com.pengfu.inote.domain.vo.admin.AdminInfoVO;
import com.pengfu.inote.domain.vo.admin.AdminVO;
import com.pengfu.inote.manager.AdminManager;
import com.pengfu.inote.mapper.AdminMapper;
import com.pengfu.inote.service.exception.ServiceException;
import com.pengfu.inote.utils.StpAdminUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AdminService {

    private AdminMapper adminMapper;

    private AdminManager adminManager;

    public TokenVO login(LoginDTO loginDTO) {
        Admin admin = adminMapper.selectOne(new QueryWrapper<Admin>().lambda()
                .eq(Admin::getUsername, loginDTO.getUsername()));

        if (admin == null) {
            throw new ServiceException(1001, "用户不存在");
        }

        if (admin.getPassword().equals(SaSecureUtil.md5(loginDTO.getPassword()))) {
            StpAdminUtil.login(admin.getId());
            SaTokenInfo tokenInfo = StpAdminUtil.getTokenInfo();
            return TokenVO.build(tokenInfo);
        } else {
            throw new ServiceException(1002, "密码错误");
        }
    }

    public void logout() {
        StpAdminUtil.logout();
    }

    public void refreshToken() {
        StpUtil.renewTimeout(86400);
    }

    /**
     * 获取所有管理员信息
     */
    public IPage<AdminVO> getList(PageDTO pageDTO) {
        return adminMapper.selectPageVo(new Page<Admin>(pageDTO.getPage(), pageDTO.getSize()));
    }

    /**
     * 获取个人信息
     */
    public AdminInfoVO getMe() {
        Long adminId = Long.valueOf(StpAdminUtil.getLoginIdAsString());
        Admin admin = adminManager.getById(adminId);
        AdminInfoVO adminInfoVO = new AdminInfoVO();
        BeanUtils.copyProperties(admin, adminInfoVO);
        return adminInfoVO;
    }

}
