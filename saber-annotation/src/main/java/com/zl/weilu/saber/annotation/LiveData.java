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
@Target({ElementType.FIELD, ElementType.TYPE})
@Documented
public @interface LiveData {
    
    /**
     * LiveDate类型
     * @return 类型
     */
    LiveDataType type() default LiveDataType.DEFAULT;

    /**
     * LiveDate保存数据类型（list, set, map）
     * @return 数据类型
     */
    LiveDataClassType classType() default LiveDataClassType.DEFAULT;
}
