package com.pengfu.inote.domain.dto.note;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class NotePostDTO {

    @ApiModelProperty(hidden = true)
    private Long userId;

    @NotBlank(message = "笔记名称不能为空")
    private String name;

    @ApiModelProperty(value = "所属文件夹ID", notes = "null:无关联文件 0:根目录")
    @Min(value = 0, message = "参数 folderId 错误")
    private Long folderId;

}
