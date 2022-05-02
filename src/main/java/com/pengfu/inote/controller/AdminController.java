package com.pengfu.inote.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pengfu.inote.domain.dto.auth.LoginDTO;
import com.pengfu.inote.domain.dto.common.PageDTO;
import com.pengfu.inote.domain.vo.common.ResultVO;
import com.pengfu.inote.domain.vo.common.TokenVO;
import com.pengfu.inote.domain.vo.admin.AdminInfoVO;
import com.pengfu.inote.domain.vo.admin.AdminVO;
import com.pengfu.inote.service.AdminService;
import com.pengfu.inote.utils.StpAdminUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "管理员")
@AllArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private AdminService adminService;

    @ApiOperation("管理员登录")
    @PostMapping("/login")
    @ApiResponses({
        @ApiResponse(code = 1001, message = "用户不存在"),
        @ApiResponse(code = 1002, message = "密码错误")
    })
    public ResultVO<TokenVO> login(@RequestBody @Validated LoginDTO loginDTO) {
        return ResultVO.success(adminService.login(loginDTO));
    }

    @ApiOperation("管理员登出")
    @PostMapping("/logout")
    public ResultVO<Void> logout() {
        adminService.logout();
        return ResultVO.success();
    }

    @ApiOperation("刷新Token")
    @PostMapping("/refreshToken")
    public ResultVO<Void> refreshToken() {
        adminService.refreshToken();
        return ResultVO.success();
    }

    @ApiOperation("添加管理员 (admin)")
    @SaCheckPermission(value = "admin-add", orRole = "admin", type = StpAdminUtil.TYPE)
    @PostMapping("")
    public ResultVO<Void> post() {
        return ResultVO.success();
    }

    @ApiOperation("获取管理员列表 (admin)")
    @SaCheckPermission(value = "admin-get", orRole = "admin", type = StpAdminUtil.TYPE)
    @GetMapping("")
    public ResultVO<IPage<AdminVO>> getList(@Validated PageDTO pageDTO) {
        return ResultVO.success(adminService.getList(pageDTO));
    }

    @ApiOperation("获取当前管理员信息")
    @SaCheckLogin(type = StpAdminUtil.TYPE)
    @GetMapping("/me")
    public ResultVO<AdminInfoVO> getMe() {
        return ResultVO.success(adminService.getMe());
    }

    @ApiOperation("删除管理员 (admin)")
    @SaCheckPermission(value = "admin-del", orRole = "admin", type = StpAdminUtil.TYPE)
    @DeleteMapping("{id:\\d+}")
    public ResultVO<Void> delete(@PathVariable Long id) {
        return ResultVO.success();
    }

}
