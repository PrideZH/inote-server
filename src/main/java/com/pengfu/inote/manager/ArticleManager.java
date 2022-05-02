package com.pengfu.inote.manager;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pengfu.inote.domain.entity.Article;
import com.pengfu.inote.domain.vo.common.ResultCode;
import com.pengfu.inote.mapper.ArticleMapper;
import com.pengfu.inote.service.exception.ServiceException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ArticleManager {

    private ArticleMapper articleMapper;

    /**
     * 通过 ID 检测文件是否存在，若不存在抛出 NOT_FOUND 错误
     */
    public void checkExist(Long id) {
        getById(id);
    }

    /**
     * 通过 ID 获取笔记，若无该则抛出 NOT_FOUND 错误
     */
    public Article getById(Long id) {
        Article article = articleMapper.selectOne(new QueryWrapper<Article>().lambda().eq(Article::getId, id));
        if (article == null) {
            throw new ServiceException(ResultCode.NOT_FOUND);
        }
        return article;
    }

}
