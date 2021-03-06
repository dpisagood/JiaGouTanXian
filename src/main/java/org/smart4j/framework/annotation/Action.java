package org.smart4j.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Action方法注解
 * Created by DP on 2016/10/27.
 */
@Target(ElementType.METHOD)//运用在方法级别的注解
@Retention(RetentionPolicy.RUNTIME)
public @interface  Action {
    String value();//请求类型和路径
}