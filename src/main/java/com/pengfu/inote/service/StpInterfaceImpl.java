package com.pengfu.inote.service;

import cn.dev33.satoken.stp.StpInterface;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pengfu.inote.domain.entity.RolePermission;
import com.pengfu.inote.domain.entity.AdminRole;
import com.pengfu.inote.domain.entity.Permission;
import com.pengfu.inote.domain.entity.Role;
import com.pengfu.inote.mapper.RolePermissionMapper;
import com.pengfu.inote.mapper.AdminRoleMapper;
import com.pengfu.inote.mapper.PermissionMapper;
import com.pengfu.inote.mapper.RoleMapper;
import com.pengfu.inote.utils.StpAdminUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限验证接口扩展
 */
@AllArgsConstructor
@Component
public class StpInterfaceImpl implements StpInterface {

    private PermissionMapper permissionMapper;
    private RolePermissionMapper adminPermissionMapper;
    private RoleMapper roleMapper;
    private AdminRoleMapper adminRoleMapper;

    /**
     * 获取指定ID管理员所有的角色对象
     */
    private List<Role> getRoleList(Object loginId) {
        List<AdminRole> adminRoles =
                adminRoleMapper.selectList(new QueryWrapper<AdminRole>().lambda()
                        .eq(AdminRole::getAdminId, loginId));
        if (adminRoles.isEmpty()) {
            return null;
        }
        List<Long> roleIds = adminRoles.stream().map(AdminRole::getRoleId).toList();
        return roleMapper.selectBatchIds(roleIds);
    }

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        if (StpAdminUtil.TYPE.equals(loginType)) {
            List<Role> roles = getRoleList(loginId);
            if (roles == null) {
                return null;
            }
            List<String> permissionNames = new ArrayList<>();
            for (Role role : roles) {
                List<RolePermission> adminPermissions =
                        adminPermissionMapper.selectList(new QueryWrapper<RolePermission>().lambda()
                                .eq(RolePermission::getRoleId, role.getId()));
                if (adminPermissions.isEmpty()) {
                    continue;
                }
                List<Long> permissionIds = adminPermissions.stream().map(RolePermission::getPermissionId).toList();
                List<Permission> permissions = permissionMapper.selectBatchIds(permissionIds);
                permissionNames.addAll(permissions.stream().map(Permission::getName).toList());
            }
            return permissionNames;
        }
        return null;
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        if (StpAdminUtil.TYPE.equals(loginType)) {
            List<Role> roles = getRoleList(loginId);
            if (roles == null) {
                return null;
            }
            return roles.stream().map(Role::getName).toList();
        }
        return null;
    }

}
