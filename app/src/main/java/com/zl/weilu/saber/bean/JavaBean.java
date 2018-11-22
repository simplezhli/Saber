package com.zl.weilu.saber.bean;

import com.zl.weilu.saber.annotation.AndroidViewModel;
import com.zl.weilu.saber.annotation.LiveData;
import com.zl.weilu.saber.annotation.LiveDataClassType;

/**
 * @Description:
 * @Author: weilu
 * @Time: 2018/11/22 0022 11:46.
 */
@AndroidViewModel
@LiveData(classType = LiveDataClassType.ARRAY_LIST)
public class JavaBean {
    
    private int age;
    private String name;
    private boolean sex;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }
}
