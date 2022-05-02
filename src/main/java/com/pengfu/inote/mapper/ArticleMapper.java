package com.pengfu.inote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pengfu.inote.domain.entity.Article;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
}
