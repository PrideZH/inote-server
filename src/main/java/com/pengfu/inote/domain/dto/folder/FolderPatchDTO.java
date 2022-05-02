package com.pengfu.inote.domain.dto.folder;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

@Data
public class FolderPatchDTO {

    @ApiModelProperty(hidden = true)
    private Long id;

    @ApiModelProperty(value = "文件夹名")
    @Length(min = 1, max = 32, message = "文件名称长度必须在1到32位")
    @Pattern(regexp = "^[^/\\\\:*?<>|\"]{1,255}$", message="文件夹名称格式错误")
    private String name;

    @ApiModelProperty(value = "父文件夹ID 0-根路径")
    @Min(value = 0, message = "参数 folderId 错误")
    private Long parentId;

}
