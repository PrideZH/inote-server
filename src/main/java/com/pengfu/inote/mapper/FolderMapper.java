package com.pengfu.inote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pengfu.inote.domain.entity.Folder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FolderMapper extends BaseMapper<Folder> {
}
