package com.pengfu.inote.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pengfu.inote.domain.dto.common.PageDTO;
import com.pengfu.inote.domain.vo.common.ResultVO;
import com.pengfu.inote.domain.vo.role.RoleVO;
import com.pengfu.inote.service.RoleService;
import com.pengfu.inote.utils.StpAdminUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "角色")
@AllArgsConstructor
@RestController
@RequestMapping("/api/role")
public class RoleController {

    private RoleService roleService;

    @ApiOperation("查询所有角色信息 (admin)")
    @SaCheckPermission(value = "role-get", orRole = "admin", type = StpAdminUtil.TYPE)
    @GetMapping("")
    public ResultVO<IPage<RoleVO>> getList(@Validated PageDTO pageDTO) {
        return ResultVO.success(roleService.getList(pageDTO));
    }

}
