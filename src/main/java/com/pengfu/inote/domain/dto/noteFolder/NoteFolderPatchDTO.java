package com.pengfu.inote.domain.dto.noteFolder;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class NoteFolderPatchDTO {

    @ApiModelProperty(hidden = true)
    private Long id;

    @ApiModelProperty(value = "关联文件夹ID 0-根路径")
    @Min(value = 0, message = "参数 folderId 错误")
    private Long folderId;

}