package com.zl.weilu.saber.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description:
 * @Author: weilu
 * @Time: 2018/6/13 0013 08:47.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
@Documented
public @interface OnChange {

    /**
     * 监听变化model名称
     */
    String model();

    /**
     * 监听模式类型
     */
    ObserveType type() default ObserveType.DEFAULT;
}
