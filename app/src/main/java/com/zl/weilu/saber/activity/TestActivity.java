package com.zl.weilu.saber.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.zl.weilu.saber.R;
import com.zl.weilu.saber.viewmodel.SeekBarViewModel;

/**
 * Activity监听Fragment数据改变
 */
public class TestActivity extends AppCompatActivity {

    SeekBarViewModel mSeekBarViewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        // 相同的key值
        mSeekBarViewModel = ViewModelProviders.of(this).get("mm", SeekBarViewModel.class);
        // 使用observeForever
        mSeekBarViewModel.getValue().observeForever(new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                Log.e("TestActivity:", integer + "");
            }
        });
    }
}
