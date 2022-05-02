package com.pengfu.inote.domain.dto.note;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Pattern;

@Data
public class NotePatchDTO {

    @ApiModelProperty(hidden = true)
    private Long id;

    @ApiModelProperty(value = "笔记名")
    @Length(min = 1, max = 64, message = "笔记名称长度必须在1到64位")
    @Pattern(regexp = "^[^/\\\\:*?<>|\"]{1,255}$", message="文件夹名称格式错误")
    private String name;

    @ToString.Exclude
    private String content;

}
