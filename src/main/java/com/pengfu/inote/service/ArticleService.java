package com.pengfu.inote.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengfu.inote.domain.dto.article.ArticlePatchDTO;
import com.pengfu.inote.domain.dto.article.ArticlePostDTO;
import com.pengfu.inote.domain.dto.common.PageDTO;
import com.pengfu.inote.domain.entity.*;
import com.pengfu.inote.domain.enums.ArticleSortTypeEnum;
import com.pengfu.inote.domain.enums.LikeTypeEnum;
import com.pengfu.inote.domain.vo.common.ResultCode;
import com.pengfu.inote.domain.vo.article.ArticleInfoVO;
import com.pengfu.inote.domain.vo.article.ArticleOpenVO;
import com.pengfu.inote.domain.vo.article.ArticlePageVO;
import com.pengfu.inote.domain.vo.user.UserOpenVO;
import com.pengfu.inote.domain.vo.user.UserPageVO;
import com.pengfu.inote.manager.*;
import com.pengfu.inote.mapper.*;
import com.pengfu.inote.mapping.ArticleMapping;
import com.pengfu.inote.mapping.PageMapping;
import com.pengfu.inote.service.exception.ServiceException;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class ArticleService {

    private ArticleMapper articleMapper;
    private LikeMapper likeMapper;
    private TagManager tagManager;
    private NoteMapper noteMapper;
    private CommentMapper commentMapper;

    private ArticleManager articleManager;
    private FileManager fileManager;
    private NoteManager noteManager;
    private UserManager userManager;
    private TagMapper tagMapper;
    private ArticleTagMapper articleTagMapper;

    /**
     * 上传公开笔记
     */
    @Transactional(rollbackFor = Exception.class)
    public ArticleInfoVO add(ArticlePostDTO articlePostDTO) throws Exception {
        Note note = noteManager.getById(articlePostDTO.getNoteId());
        if (!note.getUserId().equals(articlePostDTO.getUserId())) {
            throw new ServiceException(ResultCode.FORBIDDEN);
        }

        if (note.getArticleId() != null) {
            throw new ServiceException(1001, "文章已上传");
        }

        Article article = ArticleMapping.toArticle(articlePostDTO, () -> {
            // 文章内容
            try {
                return fileManager.read(note.getFileUrl());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        articleMapper.insert(article);

        // 更新笔记中对应的文章ID
        note.setArticleId(article.getId());
        noteMapper.updateById(note);

        // 添加标签
        if (articlePostDTO.getTags() != null && !articlePostDTO.getTags().isEmpty()) {
            ArticleTag articleTag = new ArticleTag();
            articleTag.setArticleId(article.getId());
            for (String tagName : articlePostDTO.getTags()) {
                // 获取标签对象 没有则创建
                Tag tag = tagMapper.selectOne(new QueryWrapper<Tag>().lambda().eq(Tag::getName, tagName));
                if (tag == null) {
                    tag = new Tag();
                    tag.setName(tagName);
                    tagMapper.insert(tag);
                }
                // 添加标签关联
                articleTag.setTagId(tag.getId());
                articleTagMapper.insert(articleTag);
            }
        }

        return ArticleMapping.toArticleInfoVO(article, ArrayList::new);
    }


    public IPage<ArticlePageVO> getMeList(PageDTO pageDTO, ArticleSortTypeEnum sortTypeEnum) {
        Long userId = StpUtil.getLoginIdAsLong();

        LambdaQueryWrapper<Article> lambdaWrapper = new QueryWrapper<Article>().lambda();
        lambdaWrapper.eq(Article::getUserId, userId);
        if (sortTypeEnum != null) {
            switch (sortTypeEnum) {
                case NEW -> lambdaWrapper.orderByDesc(Article::getCreateTime);
                case WATCH -> lambdaWrapper.orderByDesc(Article::getHits);
                case LIKE -> lambdaWrapper.orderByDesc(Article::getLikeCount);
            }
        }

        IPage<Article> articlePage =
                articleMapper.selectPage(new Page<>(pageDTO.getPage(), pageDTO.getSize()), lambdaWrapper);

        return PageMapping.mapping(articlePage, article -> {
            ArticlePageVO articlePageVO = ArticleMapping.toArticlePageVO(article);

            // 获取相关笔记编号
            Note note = noteMapper.selectOne(new QueryWrapper<Note>().lambda().eq(Note::getArticleId, article.getId()));
            articlePageVO.setNoteId(note.getId());

            // 获取文章标签
            articlePageVO.setTagNames(tagManager.getTagNameListByArticle(articlePageVO.getId()));

            // 统计点赞数
            Long likeCount = likeMapper.selectCount(new QueryWrapper<Like>().lambda()
                    .eq(Like::getType, LikeTypeEnum.ARTICLE)
                    .eq(Like::getTargetId, articlePageVO.getId()));
            articlePageVO.setLikeCount(likeCount);

            return articlePageVO;
        });
    }

    public ArticleInfoVO getMe(Long id) {
        Article article = articleManager.getById(id);

        Long userId = StpUtil.getLoginIdAsLong();
        if (!article.getUserId().equals(userId)) {
            throw new ServiceException(ResultCode.FORBIDDEN);
        }

        return ArticleMapping.toArticleInfoVO(article, () -> // 获取文章标签
                tagManager.getTagNameListByArticle(id)
        );
    }

    public ArticleOpenVO get(Long id) throws Exception {
        Long userId = null;
        if (StpUtil.isLogin()) {
            userId = StpUtil.getLoginIdAsLong();
        }

        Article article = articleManager.getById(id);

        ArticleOpenVO articleOpenVO = new ArticleOpenVO();
        BeanUtils.copyProperties(article, articleOpenVO);

        // 获取作者信息
        articleOpenVO.setAuthor(UserOpenVO.build(userManager.getById(article.getUserId())));

        // 获取文章标题
        List<String> sections = new ArrayList<>();
        String content = new String(article.getContent());
        if (!content.startsWith("# ")) { // 无标题内容
            sections.add(null);
        }
        BufferedReader bufferedReader = new BufferedReader(new StringReader(content));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.startsWith("# ")) {
                sections.add(line.substring(2));
            }
        }
        articleOpenVO.setSections(sections);

        // 获取文章标签
        List<ArticleTag> articleTags = articleTagMapper.selectList(new QueryWrapper<ArticleTag>().lambda()
                .eq(ArticleTag::getArticleId, id));
        if (!articleTags.isEmpty()) {
            List<Tag> tags = tagMapper.selectBatchIds(articleTags.stream().map(ArticleTag::getTagId).toList());
            articleOpenVO.setTagNames(tags.stream().map(Tag::getName).toList());
        } else {
            articleOpenVO.setTagNames(new ArrayList<>());
        }

        // TODO: 获取阅读数
        articleOpenVO.setViewCount(0L);

        // 获取点赞数
        articleOpenVO.setLikeCount(likeMapper.selectCount(new QueryWrapper<Like>().lambda()
                .eq(Like::getType, LikeTypeEnum.ARTICLE)
                .eq(Like::getTargetId, id)));

        // 获取评论数
        articleOpenVO.setCommentCount(commentMapper.selectCount(new QueryWrapper<Comment>().lambda()
                .eq(Comment::getArticleId, id)
                .eq(Comment::getDel, false)));

        // 判断当前用户是否点赞
        if (userId != null) {
            articleOpenVO.setActive(likeMapper.selectCount(new QueryWrapper<Like>().lambda()
                    .eq(Like::getUserId, userId)
                    .eq(Like::getType, LikeTypeEnum.ARTICLE)
                    .eq(Like::getTargetId, article.getId())) != 0);
        } else {
            articleOpenVO.setActive(false);
        }

        return articleOpenVO;
    }

    public String getContent(Long id, String section) throws Exception {
        Article article = articleManager.getById(id);

        StringBuilder res = new StringBuilder();

        boolean reading = false; // 是否开始读 到下个标题介绍
        if (section != null) {
            section = "# " + section;
        } else {
            reading = true; // 读无标题部分
        }

        BufferedReader bufferedReader = new BufferedReader(new StringReader(new String(article.getContent())));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.startsWith("# ")) {
                if (reading) {
                    break;
                } else if (line.equals(section)) {
                    reading = true;
                    continue; // 内容不显示章节
                }
            }
            if (reading) {
                res.append(line).append("\n");
            }
        }

        return res.toString();
    }

    /**
     * 获取文章列表
     */
    public IPage<ArticlePageVO> getList(
            PageDTO pageDTO,
            Long userId,
            List<String> keywords,
            List<String> tags,
            ArticleSortTypeEnum sortTypeEnum) {

        LambdaQueryWrapper<Article> lambdaWrapper = new QueryWrapper<Article>().lambda();
        lambdaWrapper.eq(userId != null, Article::getUserId, userId);
        lambdaWrapper.and(keywords != null && !keywords.isEmpty(), wrapper -> {
            if (keywords == null || keywords.isEmpty()) return;
            for (String keyword : keywords) {
                wrapper.like(Article::getTitle, keyword)
                        .or()
                        .like(Article::getSummary, keyword);
            }
        });
        if (sortTypeEnum != null) {
            switch (sortTypeEnum) {
                case NEW -> lambdaWrapper.orderByDesc(Article::getCreateTime);
                case WATCH -> lambdaWrapper.orderByDesc(Article::getHits);
                case LIKE -> lambdaWrapper.orderByDesc(Article::getLikeCount);
            }
        }

        IPage<Article> articlePage = articleMapper.selectPage(
                new Page<>(pageDTO.getPage(), pageDTO.getSize()), lambdaWrapper);


        return PageMapping.mapping(articlePage, article -> {
            ArticlePageVO articlePageVO = ArticleMapping.toArticlePageVO(article);

            // 获取文章标签
            articlePageVO.setTagNames(tagManager.getTagNameListByArticle(article.getId()));

            // 根据标签搜索过滤文章
            if (tags != null) {
                if (articlePageVO.getTagNames().isEmpty()) return null;
                boolean match = false;
                for (String tag : tags) {
                    match = articlePageVO.getTagNames().contains(tag);
                    if (match) break;
                }
                if (!match) return null;
            }

            // 获取笔记编号
            articlePageVO.setNoteId(
                    noteMapper.selectOne(new QueryWrapper<Note>().lambda()
                            .eq(Note::getArticleId, article.getId())).getId());

            // 获取作者信息 获取指定文章时不需获取作者消息
            if (userId == null) {
                User user = userManager.getById(article.getUserId());
                UserPageVO userPageVO = new UserPageVO();
                BeanUtils.copyProperties(user, userPageVO);
                articlePageVO.setAuthor(userPageVO);
            }

            // 统计点赞数
            Long likeCount = likeMapper.selectCount(new QueryWrapper<Like>().lambda()
                    .eq(Like::getType, LikeTypeEnum.ARTICLE)
                    .eq(Like::getTargetId, article.getId()));
            articlePageVO.setLikeCount(likeCount);

            return articlePageVO;
        });
    }

    /**
     * 修改文章
     */
    public ArticleInfoVO update(ArticlePatchDTO articlePatchDTO) throws Exception {
        Article article = articleManager.getById(articlePatchDTO.getId());

        // 更新文章内容
        Note note = null;
        if (articlePatchDTO.getNoteId() != null) {
            note = noteManager.getById(articlePatchDTO.getNoteId());
        }

        Long userId = StpUtil.getLoginIdAsLong();
        if (!article.getUserId().equals(userId)) {
            throw new ServiceException(ResultCode.FORBIDDEN);
        }

        Article noteValue = new Article();
        BeanUtils.copyProperties(articlePatchDTO, noteValue);
        if (note != null) {
            noteValue.setContent(fileManager.read(note.getFileUrl()));
        }
        articleMapper.updateById(noteValue);

        // 若修改了封面图片 且旧图片不在被应用 则删除图片文件
        if (article.getCoverUrl() != null && articlePatchDTO.getCoverUrl() != null) {
            if (articleMapper.selectCount(new QueryWrapper<Article>().lambda()
                    .eq(Article::getCoverUrl, article.getCoverUrl())) == 0) {
                fileManager.delete(article.getCoverUrl());
            }
        }

        // 更新标签
        if (articlePatchDTO.getTags() != null && !articlePatchDTO.getTags().isEmpty()) {
            // 获取当前文章标签
            List<ArticleTag> articleTags = new LambdaQueryChainWrapper<>(articleTagMapper)
                    .select(ArticleTag::getTagId).eq(ArticleTag::getArticleId, article.getId()).list();
            List<String> tagNames = new ArrayList<>();
            // 删除文章标签
            for (ArticleTag articleTag : articleTags) {
                // 若原标签没有该标签则删除
                Tag tag = tagMapper.selectById(articleTag.getTagId());
                tagNames.add(tag.getName());
                if (!articlePatchDTO.getTags().contains(tag.getName())) {
                    articleTagMapper.deleteById(articleTag.getId());
                    // 若标签不在被引用则删除
                    if (new LambdaQueryChainWrapper<>(articleTagMapper)
                            .eq(ArticleTag::getTagId, tag.getId()).count() == 0) {
                        tagMapper.deleteById(tag.getId());
                    }
                }
            }
            // 添加文章标签
            for (String tagName : articlePatchDTO.getTags()) {
                // 若修改标签在源标签上不存在则添加
                if (!tagNames.contains(tagName)) {
                    // 获取标签对象 没有则创建
                    Tag tag = tagMapper.selectOne(new QueryWrapper<Tag>().lambda().eq(Tag::getName, tagName));
                    if (tag == null) {
                        tag = new Tag();
                        tag.setName(tagName);
                        tagMapper.insert(tag);
                    }
                    // 添加标签关联
                    ArticleTag articleTag = new ArticleTag();
                    articleTag.setArticleId(article.getId());
                    articleTag.setTagId(tag.getId());
                    articleTagMapper.insert(articleTag);
                }
            }
        }

        article = articleManager.getById(articlePatchDTO.getId());
        return ArticleMapping.toArticleInfoVO(article, () -> // 获取文章标签
                tagManager.getTagNameListByArticle(articlePatchDTO.getId())
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public void del(Long id) {
        Article article = articleManager.getById(id);

        Long userId = StpUtil.getLoginIdAsLong();
        if (!article.getUserId().equals(userId)) {
            throw new ServiceException(ResultCode.FORBIDDEN);
        }

        // 删除标签
        List<ArticleTag> articleTags = articleTagMapper.selectList(new QueryWrapper<ArticleTag>().lambda()
                .eq(ArticleTag::getArticleId, id));
        if (articleTags != null && !articleTags.isEmpty()) {
            for (ArticleTag articleTag : articleTags) {
                articleTagMapper.deleteById(articleTag.getId());
                // 若标签不在被引用则删除
                if (new LambdaQueryChainWrapper<>(articleTagMapper)
                        .eq(ArticleTag::getTagId, articleTag.getTagId()).count() == 0) {
                    tagMapper.deleteById(articleTag.getTagId());
                }
            }
        }

        articleMapper.deleteById(id);
    }

}
