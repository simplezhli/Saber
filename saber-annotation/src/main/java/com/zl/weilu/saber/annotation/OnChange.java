package com.zl.weilu.saber.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据变化接收方法的注解类,运用在方法上。
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
@Documented
public @interface OnChange {

    /**
     * 监听变化model名称,
     * @return model名称
     */
    String model() default "mViewModel";

    /**
     * 监听模式类型
     * @return 类型
     */
    ObserveType type() default ObserveType.DEFAULT;
}
