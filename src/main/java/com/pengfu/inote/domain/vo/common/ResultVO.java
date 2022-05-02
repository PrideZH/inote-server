package com.pengfu.inote.domain.vo.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@ApiModel("统一返回数据结构")
@AllArgsConstructor
@Data
public class ResultVO<T> implements Serializable {

    @ApiModelProperty("状态码")
    private Integer code;

    @ApiModelProperty("响应描述")
    private String message;

    @ApiModelProperty("响应数据")
    private T data;

    public ResultVO(Integer code, String message) {
        this(code, message, null);
    }

    public ResultVO(ResultCode resultCode) {
        this(resultCode.code(), resultCode.message(), null);
    }

    public ResultVO(ResultCode resultCode, T data) {
        this(resultCode.code(), resultCode.message(), data);
    }

    public ResultVO(ResultCode resultCode, String message, T data) {
        this(resultCode.code(), message, data);
    }

    public static ResultVO<Void> success() {
        return new ResultVO<>(ResultCode.OK);
    }

    public static <T> ResultVO<T> success(T data) {
        return new ResultVO<>(ResultCode.OK, data);
    }

}
