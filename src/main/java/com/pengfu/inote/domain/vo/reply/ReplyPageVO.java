package com.pengfu.inote.domain.vo.reply;

import com.pengfu.inote.domain.vo.common.BaseVO;
import com.pengfu.inote.domain.vo.user.UserPageVO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ReplyPageVO extends BaseVO {

    private UserPageVO user;

    private UserPageVO atUser;

    private String content;

    private Long likeCount;

    private Boolean active;

}
