package com.pengfu.inote.domain.vo.comment;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pengfu.inote.domain.vo.common.BaseVO;
import com.pengfu.inote.domain.vo.reply.ReplyPageVO;
import com.pengfu.inote.domain.vo.user.UserPageVO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CommentOutlineVO extends BaseVO {

    private UserPageVO user;

    private String content;

    private Long likeCount;

    private Boolean active;

    /** 回复页数 */
    private Long page;

    private IPage<ReplyPageVO> replyPage;

}
