package com.pengfu.inote.domain.dto.common;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class PageDTO {

    @NotNull(message = "缺少 page")
    @Min(value = 1, message = "page 必须大于等于 1")
    private Integer page;

    @NotNull(message = "缺少 size")
    @Min(value = 1, message = "size 必须大于等于 1")
    private Integer size;

}
