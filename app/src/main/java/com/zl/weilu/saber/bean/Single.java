package com.zl.weilu.saber.bean;

import com.zl.weilu.saber.annotation.LiveData;
import com.zl.weilu.saber.annotation.LiveDataType;

/**
 * @Description:
 * @Author: weilu
 * @Time: 2018/7/25 0025 17:53.
 */
public class Single {

    @LiveData(type = LiveDataType.SINGLE)
    Integer value;
}
