package com.pengfu.inote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pengfu.inote.domain.entity.NoteFolder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoteFolderMapper extends BaseMapper<NoteFolder> {
}
