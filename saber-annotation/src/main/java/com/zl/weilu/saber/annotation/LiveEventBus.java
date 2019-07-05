package com.zl.weilu.saber.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * EventBus的注解类,运用在方法上。
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
@Documented
public @interface LiveEventBus {

    /**
     * 是否是Sticky模式
     * 当isBus为true时生效
     * @return
     */
    boolean isSticky() default false;

    /**
     * 监听变化model名称,
     * @return model名称
     */
    String key() default "mViewModel";

    /**
     * 监听模式类型
     * @return 类型
     */
    ObserveType type() default ObserveType.DEFAULT;
}
