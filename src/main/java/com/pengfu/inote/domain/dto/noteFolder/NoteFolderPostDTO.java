package com.pengfu.inote.domain.dto.noteFolder;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class NoteFolderPostDTO {

    @ApiModelProperty(value = "源笔记ID")
    @NotNull
    @Min(value = 1, message = "参数 noteId 错误")
    private Long noteId;

    @ApiModelProperty(value = "文件夹ID")
    @NotNull
    @Min(value = 0, message = "参数 folderId 错误")
    private Long folderId;

}
