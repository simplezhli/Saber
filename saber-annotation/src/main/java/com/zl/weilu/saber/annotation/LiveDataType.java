package com.zl.weilu.saber.annotation;

/**
 * LiveDate模式枚举
 */
public enum LiveDataType {

    /**
     * 默认模式(MutableLiveData)
     */
    DEFAULT,

    /**
     * 中介模式(MediatorLiveData)
     */
    MEDIATOR,

    /**
     * 发送一次(SingleLiveEvent)
     */
    SINGLE,

    /**
     * 其他（可自定义）
     */
    OTHER
}
