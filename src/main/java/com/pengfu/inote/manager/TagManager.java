package com.pengfu.inote.manager;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pengfu.inote.domain.entity.ArticleTag;
import com.pengfu.inote.domain.entity.Tag;
import com.pengfu.inote.mapper.ArticleTagMapper;
import com.pengfu.inote.mapper.TagMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class TagManager {

    private TagMapper tagMapper;
    private ArticleTagMapper articleTagMapper;

    public List<String> getTagNameListByArticle(Long id) {
        List<String> res;
        List<ArticleTag> articleTags = articleTagMapper.selectList(new QueryWrapper<ArticleTag>().lambda()
                .eq(ArticleTag::getArticleId, id));
        if (!articleTags.isEmpty()) {
            List<Tag> tags = tagMapper.selectBatchIds(articleTags.stream().map(ArticleTag::getTagId).toList());
            res = tags.stream().map(Tag::getName).toList();
        } else {
            res = new ArrayList<>();
        }
        return res;
    }

}
