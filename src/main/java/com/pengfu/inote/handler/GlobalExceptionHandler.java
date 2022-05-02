package com.pengfu.inote.handler;

import cn.dev33.satoken.exception.DisableLoginException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.pengfu.inote.domain.vo.common.ResultCode;
import com.pengfu.inote.domain.vo.common.ResultVO;
import com.pengfu.inote.service.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;

/**
 * 全局异常处理
 */
@AllArgsConstructor
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private HttpServletRequest request;

    @ExceptionHandler(Exception.class)
    public ResultVO<Void> handlerException(Exception e) {
        e.printStackTrace();
        log.error(e.getMessage());
        return new ResultVO<>(ResultCode.INTERNAL_SERVER_ERROR);
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(ServiceException.class)
    public ResultVO<Void> handlerException(ServiceException e) {
        log.info(e.getMessage());
        return new ResultVO<>(e.getCode(), e.getMessage());
    }

    /**
     * 未登录异常
     */
    @ExceptionHandler(NotLoginException.class)
    public ResultVO<Void> handlerNotLoginException(NotLoginException e) {
        String message = switch (e.getType()) {
            case NotLoginException.NOT_TOKEN -> "未提供token";
            case NotLoginException.INVALID_TOKEN -> "token无效";
            case NotLoginException.TOKEN_TIMEOUT -> "token已过期";
            case NotLoginException.BE_REPLACED -> "token已被顶下线";
            case NotLoginException.KICK_OUT -> "token已被踢下线";
            default -> "当前会话未登录";
        };
        log.info(message);
        return new ResultVO<>(ResultCode.UNAUTHORIZED, message, null);
    }

    /**
     * 权限异常
     */
    @ExceptionHandler(NotRoleException.class)
    public ResultVO<Void> handlerException(NotRoleException e) {
        log.info("无此角色：" + e.getRole());
        return new ResultVO<>(ResultCode.FORBIDDEN);
    }
    @ExceptionHandler(NotPermissionException.class)
    public ResultVO<Void> handlerException(NotPermissionException e) {
        log.info("无此权限：" + e.getCode());
        return new ResultVO<>(ResultCode.FORBIDDEN);
    }
    @ExceptionHandler(DisableLoginException.class)
    public ResultVO<Void> handlerException(DisableLoginException e) {
        log.info("账号被封禁：" + e.getDisableTime() + "秒后解封");
        return new ResultVO<>(ResultCode.FORBIDDEN, "当前账号被封禁", null);
    }

    /**
     * 处理请求异常 请求参数类型错误
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResultVO<List<String>> handlerMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.info("{} -> {}", request.getServletPath(), e.getMessage());
        return new ResultVO<>(ResultCode.BAD_REQUEST, "请求参数类型错误: %s".formatted(e.getName()), null);
    }

    /**
     * 处理请求异常 对象属性参数格式错误
     */
    @ExceptionHandler(BindException.class)
    public ResultVO<List<String>> handlerMethodArgumentNotValidException(BindException e) {
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        List<String> errors = allErrors.stream().map(ObjectError::getDefaultMessage).toList();
        log.info("{} -> {}", request.getServletPath(), errors.toString());
        return new ResultVO<>(ResultCode.BAD_REQUEST, "请求参数错误", errors);
    }

    /**
     * 处理请求异常 @RequestParam 请求参数格式错误异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResultVO<List<String>> handlerValidationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        List<String> errors = constraintViolations.stream().map(ConstraintViolation::getMessage).toList();
        log.info("{} -> {}", request.getServletPath(), errors.toString());
        return new ResultVO<>(ResultCode.BAD_REQUEST, "请求参数错误", errors);
    }

    /**
     * 请求 Body JSON 格式错误
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResultVO<String> handlerHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return new ResultVO<>(ResultCode.BAD_REQUEST, "JSON格式错误", null);
    }

}
