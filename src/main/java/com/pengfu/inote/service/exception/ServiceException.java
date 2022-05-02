package com.pengfu.inote.service.exception;

import com.pengfu.inote.domain.vo.common.ResultCode;

/**
 * 业务异常
 */
public class ServiceException extends RuntimeException {

    private final int code;
    private final String message;

    public ServiceException(int code, String message) {
        super(message, null, false, false); // 关闭栈追踪
        this.code = code;
        this.message = message;
    }

    public ServiceException(ResultCode resultCode) {
        super(resultCode.message(), null, false, false); // 关闭栈追踪
        this.code = resultCode.code();
        this.message = resultCode.message();
    }

    public ServiceException(ResultCode resultCode, String message) {
        super(resultCode.message(), null, false, false); // 关闭栈追踪
        this.code = resultCode.code();
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}