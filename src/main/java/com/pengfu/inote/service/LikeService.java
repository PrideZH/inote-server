package com.pengfu.inote.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pengfu.inote.domain.dto.like.LikePostDTO;
import com.pengfu.inote.domain.entity.Article;
import com.pengfu.inote.domain.entity.Comment;
import com.pengfu.inote.domain.entity.Like;
import com.pengfu.inote.domain.entity.Reply;
import com.pengfu.inote.domain.enums.LikeTypeEnum;
import com.pengfu.inote.mapper.ArticleMapper;
import com.pengfu.inote.mapper.CommentMapper;
import com.pengfu.inote.mapper.LikeMapper;
import com.pengfu.inote.mapper.ReplyMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class LikeService {

    private LikeMapper likeMapper;
    private ArticleMapper articleMapper;
    private CommentMapper commentMapper;
    private ReplyMapper replyMapper;

    public void add(LikePostDTO likePostDTO) {
        Long userId = StpUtil.getLoginIdAsLong();

        Like likeExist = likeMapper.selectOne(new QueryWrapper<Like>().lambda()
                .eq(Like::getUserId, userId)
                .eq(Like::getType, likePostDTO.getType())
                .eq(Like::getTargetId, likePostDTO.getTargetId()));

        if (likeExist == null) {
            Like like = new Like();
            BeanUtils.copyProperties(likePostDTO, like);
            like.setUserId(userId);

            likeMapper.insert(like);
        } else {
            likeMapper.deleteById(likeExist);
        }

        // 更新点赞数
        switch (likePostDTO.getType()) {
            case ARTICLE -> {
                Article article = new Article();
                article.setId(likePostDTO.getTargetId());
                article.setLikeCount(likeMapper.selectCount(new QueryWrapper<Like>().lambda()
                        .eq(Like::getType, LikeTypeEnum.ARTICLE)
                        .eq(Like::getTargetId, likePostDTO.getTargetId())));
                articleMapper.updateById(article);
            }
            case COMMENT -> {
                Comment comment = new Comment();
                comment.setId(likePostDTO.getTargetId());
                comment.setLikeCount(likeMapper.selectCount(new QueryWrapper<Like>().lambda()
                        .eq(Like::getType, LikeTypeEnum.COMMENT)
                        .eq(Like::getTargetId, likePostDTO.getTargetId())));
                commentMapper.updateById(comment);
            }
            case REPLY -> {
                Reply reply = new Reply();
                reply.setId(likePostDTO.getTargetId());
                reply.setLikeCount(likeMapper.selectCount(new QueryWrapper<Like>().lambda()
                        .eq(Like::getType, LikeTypeEnum.REPLY)
                        .eq(Like::getTargetId, likePostDTO.getTargetId())));
                replyMapper.updateById(reply);
            }
        }
    }

}
