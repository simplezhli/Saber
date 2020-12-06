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
     * LiveDate保存数据类型（list, set, map, 默认为标记类型）
     * @return 数据类型
     */
    LiveDataClassType classType() default LiveDataClassType.DEFAULT;

    /**
     * LiveDataType类型为OTHER时生效, 必须指定类型！！
     * @return 具体类型
     */
    Class<?> liveDataType() default Object.class;

    /**
     * 是否启用SavedState，仅支持androidx下的type为LiveDataType.DEFAULT
     * @return 是否启用
     */
    boolean isSavedState() default false;
}
