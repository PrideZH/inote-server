package com.pengfu.inote.domain.dto.comment;

import com.pengfu.inote.domain.entity.Comment;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
public class CommentPostDTO {

    @ApiModelProperty(value = "评论文章", required = true)
    @NotNull(message = "文章不能为空")
    private Long articleId;

    @ApiModelProperty(value = "评论内容", required = true)
    @NotNull(message = "内容不能为空")
    @Length(min = 2, max = 256)
    private String content;

    /**
     * @param userId 评论者ID
     */
    public Comment toComment(Long userId) {
        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setContent(content);
        comment.setUserId(userId);
        comment.setLikeCount(0L);
        comment.setDel(false);
        return comment;
    }

}
