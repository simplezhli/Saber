package com.zl.weilu.saber.annotation;

/**
 * LiveDate数据类型枚举
 */
public enum LiveDataClassType {

    /**
     * 默认
     */
    DEFAULT,

    /**
     * {@linkplain java.util.List}
     */
    LIST,

    /**
     * {@linkplain java.util.ArrayList}
     */
    ARRAY_LIST,

    /**
     * {@linkplain java.util.Set}
     */
    SET,

    /**
     * {@linkplain java.util.HashSet}
     */
    HASH_SET,

    /**
     * {@linkplain java.util.Map}, key 为 String
     */
    MAP,

    /**
     * {@linkplain java.util.HashMap}, key 为 String
     */
    HASH_MAP,
}
