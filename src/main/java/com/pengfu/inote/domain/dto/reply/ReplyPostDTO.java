package com.pengfu.inote.domain.dto.reply;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
public class ReplyPostDTO {

    @ApiModelProperty(value = "回复评论", required = true)
    @NotNull(message = "评论不能为空")
    private Long commentId;

    @ApiModelProperty(value = "回复对象")
    private Long toUserId;

    @ApiModelProperty(value = "评论内容", required = true)
    @NotNull(message = "内容不能为空")
    @Length(min = 2, max = 256)
    private String content;

}
