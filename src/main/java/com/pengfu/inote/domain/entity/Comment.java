package com.pengfu.inote.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Comment extends BaseEntity {

    private Long userId;

    private Long articleId;

    private String content;

    private Long likeCount;

    private Boolean del;

}
