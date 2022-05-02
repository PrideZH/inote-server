package com.pengfu.inote.handler;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * 日志切面
 */
@AllArgsConstructor
@Aspect
@Component
@Slf4j
public class LogAspect {

    private HttpServletRequest request;

    @Pointcut("execution(public * com.pengfu.inote.controller..*.*(..))")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // 请求 controller 名称
        String controllerTitle = getControllerMethodTitle(joinPoint);
        // 方法路径
        String methodName = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        // IP地址
        String iP = getIp(request);
        // 请求入参
        String requestParam = Arrays.toString(joinPoint.getArgs());

        log.info("\n    [Controller start], {}, methodName->{}, IP->{}, requestParam->{}",
                controllerTitle, methodName, iP, requestParam);

        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        // 是否进行结果打印
        if (isResLog(joinPoint)) {
            log.info("\n    [Controller end], {}, 耗时->{}ms, result->{}",
                    controllerTitle, System.currentTimeMillis() - startTime, result.toString());
        } else {
            log.info("\n    [Controller end], {}, 耗时->{}ms, result->[NotLog]",
                    controllerTitle, System.currentTimeMillis() - startTime);
        }
        return result;
    }

    /**
     * 通过 @ApiOperation 获取 Controller 的方法名
     */
    private String getControllerMethodTitle(ProceedingJoinPoint joinPoint) {
        Method[] methods = joinPoint.getSignature().getDeclaringType().getMethods();
        for (Method method : methods) {
            if (StringUtils.equalsIgnoreCase(method.getName(), joinPoint.getSignature().getName())) {
                ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
                if (ObjectUtils.isNotEmpty(apiOperation) && StringUtils.isNotBlank(apiOperation.value())) {
                    return apiOperation.value();
                }
            }
        }
        return "Not ApiOperation";
    }

    /**
     * 获取目标主机的ip
     */
    private String getIp(HttpServletRequest request) {
        List<String> ipHeadList =
                Stream.of("X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "X-Real-IP")
                        .toList();
        for (String ipHead : ipHeadList) {
            if (checkIP(request.getHeader(ipHead))) {
                return request.getHeader(ipHead).split(",")[0];
            }
        }
        return "0:0:0:0:0:0:0:1".equals(request.getRemoteAddr()) ? "127.0.0.1" : request.getRemoteAddr();
    }

    /**
     * 检查ip存在
     */
    private boolean checkIP(String ip) {
        return !(null == ip || 0 == ip.length() || "unknown".equalsIgnoreCase(ip));
    }

    private boolean isResLog(ProceedingJoinPoint joinPoint) {
        Method[] methods = joinPoint.getSignature().getDeclaringType().getMethods();
        for (Method method : methods) {
            if (StringUtils.equalsIgnoreCase(method.getName(), joinPoint.getSignature().getName())) {
                NotControllerResLog notControllerLog = method.getAnnotation(NotControllerResLog.class);
                if (ObjectUtils.isNotEmpty(notControllerLog)) {
                    return false;
                }
            }
        }
        return true;
    }

}
