package com.travel.backtravel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {
    /** 操作描述，如 "修改酒店" */
    String value();

    /** 模块名，如 "酒店管理" */
    String module() default "";

    /** 操作类型 */
    String type() default "";
}
