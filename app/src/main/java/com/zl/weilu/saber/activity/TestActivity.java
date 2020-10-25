package com.zl.weilu.saber.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.jeremyliao.liveeventbus.LiveEventBus;
import com.zl.weilu.saber.R;
import com.zl.weilu.saber.annotation.BindViewModel;
import com.zl.weilu.saber.annotation.ObserveType;
import com.zl.weilu.saber.annotation.OnChange;
import com.zl.weilu.saber.api.Saber;
import com.zl.weilu.saber.viewmodel.SeekBarViewModel;

/**
 * Activity监听Fragment数据改变
 */
public class TestActivity extends AppCompatActivity {

    @BindViewModel(key = "mm")
    SeekBarViewModel mViewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Saber.bind(this);
        LiveEventBus.get("key_name").postDelay("----1111", 2000);
    }

    // 使用observeForever
    // 不设置model，默认为mViewModel。
    @OnChange(type = ObserveType.FOREVER)
    void setData(Integer value){
        if (value != null) {
            Log.e("TestActivity:", "Fragment1，2中，SeekBar的数值：" + value);
        }
    }
}
