package com.pengfu.inote.mapping;

import com.pengfu.inote.domain.dto.article.ArticlePostDTO;
import com.pengfu.inote.domain.entity.Article;
import com.pengfu.inote.domain.vo.article.ArticleInfoVO;
import com.pengfu.inote.domain.vo.article.ArticlePageVO;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.function.Supplier;

public class ArticleMapping {

    public static Article toArticle(ArticlePostDTO source, Supplier<byte[]> content) {
        Article target = new Article();

        BeanUtils.copyProperties(source, target);
        target.setContent(content.get());
        target.setLikeCount(0L);
        target.setHits(0L);

        return target;
    }

    public static ArticleInfoVO toArticleInfoVO(Article source, Supplier<List<String>> tagNames) {
        ArticleInfoVO target = new ArticleInfoVO();

        target.setId(source.getId());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());

        target.setTitle(source.getTitle());
        target.setSummary(source.getTitle());
        target.setCoverUrl(source.getCoverUrl());
        target.setTagNames(tagNames.get());
        target.setStatus(source.getStatus());

        return target;
    }

    public static ArticlePageVO toArticlePageVO(Article source) {
        ArticlePageVO target = new ArticlePageVO();

        target.setId(source.getId());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());

        target.setUserId(source.getUserId());
        target.setCoverUrl(source.getCoverUrl());
        target.setSummary(source.getSummary());
        target.setTitle(source.getTitle());

        return target;
    }

}
