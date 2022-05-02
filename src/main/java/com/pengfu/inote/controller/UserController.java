package com.pengfu.inote.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pengfu.inote.domain.dto.auth.LoginDTO;
import com.pengfu.inote.domain.dto.auth.RegisterDTO;
import com.pengfu.inote.domain.dto.auth.EmailCodeDTO;
import com.pengfu.inote.domain.dto.common.PageDTO;
import com.pengfu.inote.domain.dto.user.UserPatchDTO;
import com.pengfu.inote.domain.vo.common.ResultVO;
import com.pengfu.inote.domain.vo.common.TokenVO;
import com.pengfu.inote.domain.vo.user.UserInfoVO;
import com.pengfu.inote.domain.vo.user.UserOpenVO;
import com.pengfu.inote.domain.vo.user.UserVO;
import com.pengfu.inote.service.UserService;
import com.pengfu.inote.utils.StpAdminUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "用户")
@AllArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

    private UserService userService;

    @ApiOperation("用户注册")
    @ApiResponses({
        @ApiResponse(code = 1001, message = "用户已存在"),
        @ApiResponse(code = 1002, message = "验证码错误")
    })
    @PostMapping("/register")
    public ResultVO<Void> register(@RequestBody @Validated RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return ResultVO.success();
    }

    // TODO: 获取用户账号是否存在 用于用户注册时判断

    @ApiOperation("生成验证码")
    @PostMapping("/register/code")
    @ApiResponses({
            @ApiResponse(code = 1001, message = "该邮箱已注册")
    })
    public ResultVO<Void> getCode(@RequestBody @Validated EmailCodeDTO emailCodeDTO) {
        userService.getCode(emailCodeDTO);
        return ResultVO.success();
    }

    @ApiOperation("用户登录")
    @ApiResponses({
        @ApiResponse(code = 1001, message = "用户不存在"),
        @ApiResponse(code = 1002, message = "密码错误")
    })
    @PostMapping("/login")
    public ResultVO<TokenVO> login(@RequestBody @Validated LoginDTO loginDTO) {
        return ResultVO.success(userService.login(loginDTO));
    }

    @ApiOperation("用户登出")
    @PostMapping("/logout")
    public ResultVO<Void> logout() {
        userService.logout();
        return ResultVO.success();
    }

    @ApiOperation("刷新Token")
    @PostMapping("/refreshToken")
    public ResultVO<Void> refreshToken() {
        userService.refreshToken();
        return ResultVO.success();
    }

    @ApiOperation("查询所有用户信息 (admin)")
    @SaCheckLogin(type = StpAdminUtil.TYPE)
    @GetMapping()
    public ResultVO<IPage<UserVO>> getList(@Validated PageDTO pageDTO) {
        return ResultVO.success(userService.getList(pageDTO));
    }

    @ApiOperation("获取当前用户信息")
    @SaCheckLogin
    @GetMapping("/me")
    public ResultVO<UserInfoVO> getMe() {
        return ResultVO.success(userService.getMe());
    }

    @ApiOperation("获取指定用户的公开数据")
    @GetMapping("/{id:\\d+}/open")
    public ResultVO<UserOpenVO> getOpen(@PathVariable Long id) {
        return ResultVO.success(userService.getOpen(id));
    }

    @ApiOperation(value = "修改用户信息")
    @SaCheckLogin
    @PatchMapping("/{id:\\d+}")
    public ResultVO<UserInfoVO> patch(@PathVariable Long id, @RequestBody @Validated UserPatchDTO userPatchDTO) {
        userPatchDTO.setId(id);
        return ResultVO.success(userService.update(userPatchDTO));
    }

}
