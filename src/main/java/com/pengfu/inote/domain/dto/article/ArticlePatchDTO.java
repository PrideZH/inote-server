package com.pengfu.inote.domain.dto.article;

import com.pengfu.inote.domain.enums.StatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
public class ArticlePatchDTO {

    @ApiModelProperty(hidden = true)
    private Long id;

    @ApiModelProperty(value = "笔记标题", required = true)
    @Length(min = 1, max = 64, message = "文件名称长度必须在1到64位")
    private String title;

    @ApiModelProperty(value = "笔记ID", notes = "不为空则更新文章内容为该笔记内容")
    private Long noteId;

    @ApiModelProperty(value = "简介")
    @Length(max = 256, message = "简介长度不能超过256")
    private String summary;

    @ApiModelProperty(value = "状态")
    private StatusEnum status;

    @ApiModelProperty(value = "封面")
    private String coverUrl;

    @ApiModelProperty(value = "标签列表")
    private List<String> tags;

}
