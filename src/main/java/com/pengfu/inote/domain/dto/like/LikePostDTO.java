package com.pengfu.inote.domain.dto.like;

import com.pengfu.inote.domain.enums.LikeTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LikePostDTO {

    @ApiModelProperty(value = "目标ID", required = true)
    @NotNull(message = "目标ID不能为空")
    private Long targetId;

    @ApiModelProperty(value = "目标类型", required = true)
    @NotNull(message = "目标类型不能为空")
    private LikeTypeEnum type;

}
