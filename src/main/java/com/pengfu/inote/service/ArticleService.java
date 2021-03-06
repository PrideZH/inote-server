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
     * ??????????????????
     */
    @Transactional(rollbackFor = Exception.class)
    public ArticleInfoVO add(ArticlePostDTO articlePostDTO) throws Exception {
        Note note = noteManager.getById(articlePostDTO.getNoteId());
        if (!note.getUserId().equals(articlePostDTO.getUserId())) {
            throw new ServiceException(ResultCode.FORBIDDEN);
        }

        if (note.getArticleId() != null) {
            throw new ServiceException(1001, "???????????????");
        }

        Article article = ArticleMapping.toArticle(articlePostDTO, () -> {
            // ????????????
            try {
                return fileManager.read(note.getFileUrl());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        articleMapper.insert(article);

        // ??????????????????????????????ID
        note.setArticleId(article.getId());
        noteMapper.updateById(note);

        // ????????????
        if (articlePostDTO.getTags() != null && !articlePostDTO.getTags().isEmpty()) {
            ArticleTag articleTag = new ArticleTag();
            articleTag.setArticleId(article.getId());
            for (String tagName : articlePostDTO.getTags()) {
                // ?????????????????? ???????????????
                Tag tag = tagMapper.selectOne(new QueryWrapper<Tag>().lambda().eq(Tag::getName, tagName));
                if (tag == null) {
                    tag = new Tag();
                    tag.setName(tagName);
                    tagMapper.insert(tag);
                }
                // ??????????????????
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

            // ????????????????????????
            Note note = noteMapper.selectOne(new QueryWrapper<Note>().lambda().eq(Note::getArticleId, article.getId()));
            articlePageVO.setNoteId(note.getId());

            // ??????????????????
            articlePageVO.setTagNames(tagManager.getTagNameListByArticle(articlePageVO.getId()));

            // ???????????????
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

        return ArticleMapping.toArticleInfoVO(article, () -> // ??????????????????
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

        // ??????????????????
        articleOpenVO.setAuthor(UserOpenVO.build(userManager.getById(article.getUserId())));

        // ??????????????????
        List<String> sections = new ArrayList<>();
        String content = new String(article.getContent());
        if (!content.startsWith("# ")) { // ???????????????
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

        // ??????????????????
        List<ArticleTag> articleTags = articleTagMapper.selectList(new QueryWrapper<ArticleTag>().lambda()
                .eq(ArticleTag::getArticleId, id));
        if (!articleTags.isEmpty()) {
            List<Tag> tags = tagMapper.selectBatchIds(articleTags.stream().map(ArticleTag::getTagId).toList());
            articleOpenVO.setTagNames(tags.stream().map(Tag::getName).toList());
        } else {
            articleOpenVO.setTagNames(new ArrayList<>());
        }

        // TODO: ???????????????
        articleOpenVO.setViewCount(0L);

        // ???????????????
        articleOpenVO.setLikeCount(likeMapper.selectCount(new QueryWrapper<Like>().lambda()
                .eq(Like::getType, LikeTypeEnum.ARTICLE)
                .eq(Like::getTargetId, id)));

        // ???????????????
        articleOpenVO.setCommentCount(commentMapper.selectCount(new QueryWrapper<Comment>().lambda()
                .eq(Comment::getArticleId, id)
                .eq(Comment::getDel, false)));

        // ??????????????????????????????
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

        boolean reading = false; // ??????????????? ?????????????????????
        if (section != null) {
            section = "# " + section;
        } else {
            reading = true; // ??????????????????
        }

        BufferedReader bufferedReader = new BufferedReader(new StringReader(new String(article.getContent())));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.startsWith("# ")) {
                if (reading) {
                    break;
                } else if (line.equals(section)) {
                    reading = true;
                    continue; // ?????????????????????
                }
            }
            if (reading) {
                res.append(line).append("\n");
            }
        }

        return res.toString();
    }

    /**
     * ??????????????????
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

            // ??????????????????
            articlePageVO.setTagNames(tagManager.getTagNameListByArticle(article.getId()));

            // ??????????????????????????????
            if (tags != null) {
                if (articlePageVO.getTagNames().isEmpty()) return null;
                boolean match = false;
                for (String tag : tags) {
                    match = articlePageVO.getTagNames().contains(tag);
                    if (match) break;
                }
                if (!match) return null;
            }

            // ??????????????????
            articlePageVO.setNoteId(
                    noteMapper.selectOne(new QueryWrapper<Note>().lambda()
                            .eq(Note::getArticleId, article.getId())).getId());

            // ?????????????????? ?????????????????????????????????????????????
            if (userId == null) {
                User user = userManager.getById(article.getUserId());
                UserPageVO userPageVO = new UserPageVO();
                BeanUtils.copyProperties(user, userPageVO);
                articlePageVO.setAuthor(userPageVO);
            }

            // ???????????????
            Long likeCount = likeMapper.selectCount(new QueryWrapper<Like>().lambda()
                    .eq(Like::getType, LikeTypeEnum.ARTICLE)
                    .eq(Like::getTargetId, article.getId()));
            articlePageVO.setLikeCount(likeCount);

            return articlePageVO;
        });
    }

    /**
     * ????????????
     */
    public ArticleInfoVO update(ArticlePatchDTO articlePatchDTO) throws Exception {
        Article article = articleManager.getById(articlePatchDTO.getId());

        // ??????????????????
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

        // ???????????????????????? ??????????????????????????? ?????????????????????
        if (article.getCoverUrl() != null && articlePatchDTO.getCoverUrl() != null) {
            if (articleMapper.selectCount(new QueryWrapper<Article>().lambda()
                    .eq(Article::getCoverUrl, article.getCoverUrl())) == 0) {
                fileManager.delete(article.getCoverUrl());
            }
        }

        // ????????????
        if (articlePatchDTO.getTags() != null && !articlePatchDTO.getTags().isEmpty()) {
            // ????????????????????????
            List<ArticleTag> articleTags = new LambdaQueryChainWrapper<>(articleTagMapper)
                    .select(ArticleTag::getTagId).eq(ArticleTag::getArticleId, article.getId()).list();
            List<String> tagNames = new ArrayList<>();
            // ??????????????????
            for (ArticleTag articleTag : articleTags) {
                // ????????????????????????????????????
                Tag tag = tagMapper.selectById(articleTag.getTagId());
                tagNames.add(tag.getName());
                if (!articlePatchDTO.getTags().contains(tag.getName())) {
                    articleTagMapper.deleteById(articleTag.getId());
                    // ?????????????????????????????????
                    if (new LambdaQueryChainWrapper<>(articleTagMapper)
                            .eq(ArticleTag::getTagId, tag.getId()).count() == 0) {
                        tagMapper.deleteById(tag.getId());
                    }
                }
            }
            // ??????????????????
            for (String tagName : articlePatchDTO.getTags()) {
                // ????????????????????????????????????????????????
                if (!tagNames.contains(tagName)) {
                    // ?????????????????? ???????????????
                    Tag tag = tagMapper.selectOne(new QueryWrapper<Tag>().lambda().eq(Tag::getName, tagName));
                    if (tag == null) {
                        tag = new Tag();
                        tag.setName(tagName);
                        tagMapper.insert(tag);
                    }
                    // ??????????????????
                    ArticleTag articleTag = new ArticleTag();
                    articleTag.setArticleId(article.getId());
                    articleTag.setTagId(tag.getId());
                    articleTagMapper.insert(articleTag);
                }
            }
        }

        article = articleManager.getById(articlePatchDTO.getId());
        return ArticleMapping.toArticleInfoVO(article, () -> // ??????????????????
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

        // ????????????
        List<ArticleTag> articleTags = articleTagMapper.selectList(new QueryWrapper<ArticleTag>().lambda()
                .eq(ArticleTag::getArticleId, id));
        if (articleTags != null && !articleTags.isEmpty()) {
            for (ArticleTag articleTag : articleTags) {
                articleTagMapper.deleteById(articleTag.getId());
                // ?????????????????????????????????
                if (new LambdaQueryChainWrapper<>(articleTagMapper)
                        .eq(ArticleTag::getTagId, articleTag.getTagId()).count() == 0) {
                    tagMapper.deleteById(articleTag.getTagId());
                }
            }
        }

        articleMapper.deleteById(id);
    }

}
