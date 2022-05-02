package com.pengfu.inote.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Reply extends BaseEntity {

    private Long commentId;

    private Long fromUserId;

    private Long toUserId;

    private String content;

    private Long likeCount;

    private Boolean del;

}
