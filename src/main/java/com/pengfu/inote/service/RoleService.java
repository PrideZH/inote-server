package com.pengfu.inote.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengfu.inote.domain.dto.common.PageDTO;
import com.pengfu.inote.domain.entity.Admin;
import com.pengfu.inote.domain.vo.role.RoleVO;
import com.pengfu.inote.mapper.RoleMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class RoleService {

    private RoleMapper roleMapper;

    /**
     * 获取所有角色对象
     */
    public IPage<RoleVO> getList(PageDTO pageDTO) {
        return roleMapper.selectPageVo(new Page<Admin>(pageDTO.getPage(), pageDTO.getSize()));
    }
}
