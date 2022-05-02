package com.pengfu.inote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengfu.inote.domain.entity.Admin;
import com.pengfu.inote.domain.vo.admin.AdminVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminMapper extends BaseMapper<Admin> {

    @ResultType(AdminVO.class)
    @Select("SELECT * FROM admin")
    IPage<AdminVO> selectPageVo(Page<?> page);

}
