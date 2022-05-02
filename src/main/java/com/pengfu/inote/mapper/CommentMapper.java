package com.pengfu.inote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pengfu.inote.domain.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
