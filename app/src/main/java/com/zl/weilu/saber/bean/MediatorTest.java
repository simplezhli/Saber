package com.zl.weilu.saber.bean;

import com.zl.weilu.saber.annotation.LiveData;
import com.zl.weilu.saber.annotation.LiveDataType;

public class MediatorTest {

    @LiveData(type = LiveDataType.MEDIATOR)
    String value;
}
