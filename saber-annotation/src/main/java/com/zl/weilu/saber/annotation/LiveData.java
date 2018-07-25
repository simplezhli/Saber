package com.zl.weilu.saber.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记数据的注解类。
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
@Documented
public @interface LiveData {
    
    /**
     * LiveDate类型
     * @return 类型
     */
    LiveDateType type() default LiveDateType.DEFAULT;
}
