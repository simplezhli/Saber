package com.zl.weilu.saber.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
@Documented
public @interface BindViewModel {

    /**
     * 获取ViewModel时指定key
     */
    String key() default "";

    /**
     * 适用于Fragment，是否数据共享。
     */
    boolean isShare() default false;
}
