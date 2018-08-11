package com.zl.weilu.saber.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.zl.weilu.saber.R;
import com.zl.weilu.saber.annotation.BindViewModel;
import com.zl.weilu.saber.annotation.OnChange;
import com.zl.weilu.saber.api.Saber;
import com.zl.weilu.saber.api.UnBinder;
import com.zl.weilu.saber.viewmodel.LiveDataTimerViewModel;
import com.zl.weilu.saber.viewmodel.SingleViewModel;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private SeekBar mSeekBar;
    private UnBinder mUnBinder;
    
    @BindViewModel
    LiveDataTimerViewModel mTimerViewModel;
    @BindViewModel
    SingleViewModel mSingleViewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        textView = this.findViewById(R.id.tv);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TestActivity.class));
            }
        });

        mSeekBar = this.findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mSingleViewModel.setValue(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        mUnBinder = Saber.bind(this);
    }

    @OnChange(model = "mTimerViewModel")
    void setData(Long time){
        String newText = MainActivity.this.getResources().getString(R.string.seconds, time);
        textView.setText(newText);
        Log.d("MainActivity", "Updating timer");
    }

    /**
     * SingleViewModel的变动每次只能发送一次
     * 添加了多个，则只会有一个响应变化
     * */
    
    @OnChange(model = "mSingleViewModel")
    void setData(Integer value){
        if (value != null) {
            Log.d("MainActivity", "监听一号SeekBar的数值：" + value);
        }
    }

    @OnChange(model = "mSingleViewModel")
    void setData1(Integer value){
        if (value != null) {
            Log.d("MainActivity", "监听二号SeekBar的数值：" + value);
        }
    }

    /**
     * isBus = true表示使用事件总线方式
     * 可以使用LiveDataBus.get().with("key_name").postValue("") 来发送事件。
     * */
    @OnChange(model = "key_name", isBus = true)
    void liveDataBus(String value){
        Log.d("MainActivity", "LiveDataBus接收到的值：" +value);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnBinder != null){
            mUnBinder.unbind();
        }
    }
}
