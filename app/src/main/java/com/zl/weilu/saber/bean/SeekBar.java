package com.zl.weilu.saber.bean;

import com.zl.weilu.saber.annotation.LiveData;

/**
 * @Description:
 * @Author: weilu
 * @Time: 2018/6/11 0011 17:13.
 */
public class SeekBar {
    
    @LiveData(isSavedState = true) // 是否启用SavedState
    Integer value;

    @LiveData
    Integer value1;
}
