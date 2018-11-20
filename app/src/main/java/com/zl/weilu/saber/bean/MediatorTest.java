package com.zl.weilu.saber.bean;

import com.zl.weilu.saber.annotation.LiveData;
import com.zl.weilu.saber.annotation.LiveDataType;

import java.util.List;

public class MediatorTest {

    @LiveData(type = LiveDataType.MEDIATOR)
    String value;

    @LiveData(type = LiveDataType.MEDIATOR)
    String name;

    @LiveData
    List<String> list;
}
