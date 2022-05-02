package com.pengfu.inote.mapping;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pengfu.inote.domain.entity.Comment;
import com.pengfu.inote.domain.vo.comment.CommentOutlineVO;
import com.pengfu.inote.domain.vo.reply.ReplyPageVO;
import com.pengfu.inote.domain.vo.user.UserPageVO;

import java.util.function.Supplier;

public class CommentMapping {

    public static CommentOutlineVO toCommentOutlineVO(
            Comment source,
            Supplier<UserPageVO> user,
            Supplier<Boolean> active,
            Supplier<IPage<ReplyPageVO>> replyPage) {

        CommentOutlineVO target = new CommentOutlineVO();

        target.setId(source.getId());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());

        target.setContent(source.getContent());
        target.setLikeCount(source.getLikeCount());
        target.setPage(1L);

        target.setUser(user.get());
        target.setActive(active.get());
        target.setReplyPage(replyPage.get());

        return target;
    }

}
