package com.pengfu.inote.domain.dto.column;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@Data
public class ColumnPostDTO {

    @ApiModelProperty(value = "专栏名称", required = true)
    @NotNull(message = "专栏名称不能为空")
    @Length(min = 1, max = 64, message = "专栏名称长度必须在1到64位")
    private String name;

    @ApiModelProperty(value = "描述")
    @Max(value = 256, message = "描述字符不能超过256位")
    private String desc;

}
