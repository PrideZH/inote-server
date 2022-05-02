package com.pengfu.inote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengfu.inote.domain.entity.Tag;
import com.pengfu.inote.domain.vo.tag.TagPageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    @ResultType(TagPageVO.class)
    @Select("SELECT " +
            "*, (SELECT COUNT(*) FROM article__tag WHERE article__tag.tag_id = tag.id) article_count " +
            "FROM tag " +
            "ORDER BY article_count DESC")
    IPage<TagPageVO> selectPageVo(Page<?> page);

}
