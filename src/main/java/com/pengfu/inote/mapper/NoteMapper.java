package com.pengfu.inote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengfu.inote.domain.entity.Note;
import com.pengfu.inote.mapper.dynaSql.NoteDynaSqlProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

@Mapper
public interface NoteMapper extends BaseMapper<Note> {
}
