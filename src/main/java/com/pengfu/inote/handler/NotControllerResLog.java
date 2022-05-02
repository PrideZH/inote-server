package com.pengfu.inote.handler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解 Controller 方法
 * 不进行日志的打印
 * see: com.pengfu.inote.handler.LogAspect
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotControllerResLog {
}
