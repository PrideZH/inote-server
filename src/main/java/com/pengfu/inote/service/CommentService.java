package com.pengfu.inote.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengfu.inote.domain.dto.comment.CommentPostDTO;
import com.pengfu.inote.domain.dto.common.PageDTO;
import com.pengfu.inote.domain.entity.Comment;
import com.pengfu.inote.domain.entity.Like;
import com.pengfu.inote.domain.entity.Reply;
import com.pengfu.inote.domain.entity.User;
import com.pengfu.inote.domain.enums.LikeTypeEnum;
import com.pengfu.inote.domain.enums.SortEnum;
import com.pengfu.inote.domain.vo.comment.CommentOutlineVO;
import com.pengfu.inote.domain.vo.common.ResultCode;
import com.pengfu.inote.domain.vo.reply.ReplyPageVO;
import com.pengfu.inote.domain.vo.user.UserPageVO;
import com.pengfu.inote.manager.UserManager;
import com.pengfu.inote.mapper.CommentMapper;
import com.pengfu.inote.mapper.LikeMapper;
import com.pengfu.inote.mapper.ReplyMapper;
import com.pengfu.inote.mapping.CommentMapping;
import com.pengfu.inote.service.exception.ServiceException;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class CommentService {

    private CommentMapper commentMapper;
    private ReplyMapper replyMapper;
    private LikeMapper likeMapper;

    private UserManager userManager;

    public CommentOutlineVO add(CommentPostDTO commentPostDTO) {
        Long userId = StpUtil.getLoginIdAsLong();

        Comment comment = commentPostDTO.toComment(userId);
        commentMapper.insert(comment);

        return CommentMapping.toCommentOutlineVO(comment, () -> { // 获取评论用户
            User user = userManager.getById(comment.getUserId());
            UserPageVO userPageVO = new UserPageVO();
            BeanUtils.copyProperties(user, userPageVO);
            return userPageVO;
        }, () -> true // 该用户是否点赞
        , Page::new // 回复默认为空
        );
    }

    public IPage<CommentOutlineVO> getByArticle(PageDTO pageDTO, Long articleId, SortEnum sortEnum) {
        Long userId = null;
        if (StpUtil.isLogin()) {
            userId = StpUtil.getLoginIdAsLong();
        }

        IPage<Comment> commentPage;
        if (sortEnum == SortEnum.NEW) {
            commentPage = commentMapper.selectPage(new Page<>(pageDTO.getPage(), pageDTO.getSize()),
                    new QueryWrapper<Comment>().lambda()
                            .eq(Comment::getArticleId, articleId)
                            .eq(Comment::getDel, false)
                            .orderByDesc(Comment::getCreateTime));
        } else if (sortEnum == SortEnum.HOT) {
            commentPage = commentMapper.selectPage(new Page<>(pageDTO.getPage(), pageDTO.getSize()),
                    new QueryWrapper<Comment>().lambda()
                            .eq(Comment::getArticleId, articleId)
                            .eq(Comment::getDel, false)
                            .orderByDesc(Comment::getLikeCount));
        } else {
            throw new ServiceException(ResultCode.INTERNAL_SERVER_ERROR);
        }

        IPage<CommentOutlineVO> commentPageVOPage = new Page<>();
        BeanUtils.copyProperties(commentPage, commentPageVOPage);
        Long finalUserId = userId;
        commentPageVOPage.setRecords(commentPage.getRecords().stream().map(comment -> {
            if (comment.getDel()) return null;

            return CommentMapping.toCommentOutlineVO(comment, () -> { // 获取评论用户
                User user = userManager.getById(comment.getUserId());
                UserPageVO userPageVO = new UserPageVO();
                BeanUtils.copyProperties(user, userPageVO);
                return userPageVO;
            }, () -> { // 判断当前用户是否点赞
                if (finalUserId != null) {
                    return likeMapper.selectCount(new QueryWrapper<Like>().lambda()
                            .eq(Like::getUserId, finalUserId)
                            .eq(Like::getType, LikeTypeEnum.COMMENT)
                            .eq(Like::getTargetId, comment.getId())) != 0;
                } else {
                    return false;
                }
            }, () -> { // 获取所有回复
                Page<Reply> replyPage = replyMapper.selectPage(new Page<>(1, 6),
                        new QueryWrapper<Reply>().lambda()
                                .eq(Reply::getCommentId, comment.getId())
                                .eq(Reply::getDel, false)
                                .orderByDesc(Reply::getCreateTime));
                IPage<ReplyPageVO> replyPageVOPage = new Page<>();
                BeanUtils.copyProperties(replyPage, replyPageVOPage);
                replyPageVOPage.setRecords(replyPage.getRecords().stream().map(reply -> {
                    ReplyPageVO replyPageVO = new ReplyPageVO();
                    BeanUtils.copyProperties(reply, replyPageVO);

                    // 回复用户
                    replyPageVO.setUser(UserPageVO.build(userManager.getById(reply.getFromUserId())));

                    // @ 用户
                    replyPageVO.setAtUser(UserPageVO.build(userManager.getById(reply.getToUserId())));

                    // 判断当前用户是否点赞
                    if (finalUserId != null) {
                        replyPageVO.setActive(likeMapper.selectCount(new QueryWrapper<Like>().lambda()
                                .eq(Like::getUserId, finalUserId)
                                .eq(Like::getType, LikeTypeEnum.REPLY)
                                .eq(Like::getTargetId, reply.getId())) != 0);
                    } else {
                        replyPageVO.setActive(false);
                    }

                    return replyPageVO;
                }).toList());
                return replyPageVOPage;
            });
        }).toList());

        return commentPageVOPage;
    }

    @Transactional(rollbackFor = Exception.class)
    public void del(Long id) {
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            throw new ServiceException(ResultCode.NOT_FOUND);
        }

        Long userId = StpUtil.getLoginIdAsLong();
        if (!comment.getUserId().equals(userId)) {
            throw new ServiceException(ResultCode.FORBIDDEN);
        }

        comment.setDel(true);
        commentMapper.updateById(comment);
    }
}
