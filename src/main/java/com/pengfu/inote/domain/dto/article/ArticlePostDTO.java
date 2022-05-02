package com.pengfu.inote.domain.dto.article;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ArticlePostDTO {

    @ApiModelProperty(value = "文章标题", required = true)
    @NotNull(message = "标题不能为空")
    @Length(min = 1, max = 64, message = "文件名称长度必须在1到64位")
    private String title;

    @ApiModelProperty(hidden = true)
    private Long userId;

    @ApiModelProperty(value = "笔记ID")
    @NotNull(message = "笔记不能为空")
    private Long noteId;

    @ApiModelProperty(value = "封面地址")
    private String coverUrl;

    @ApiModelProperty(value = "简介")
    @Length(max = 256, message = "简介长度不能超过256")
    private String summary;

    @ApiModelProperty(value = "标签列表")
    private List<String> tags;

}
