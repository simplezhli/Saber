package com.zl.weilu.saber.activity;

import android.os.Bundle;
import android.util.Log;

import com.zl.weilu.saber.R;
import com.zl.weilu.saber.annotation.BindViewModel;
import com.zl.weilu.saber.annotation.OnChange;
import com.zl.weilu.saber.api.Saber;
import com.zl.weilu.saber.viewmodel.MediatorTestViewModel;
import com.zl.weilu.saber.viewmodel.SeekBarViewModel;
import com.zl.weilu.saber.viewmodel.TimerViewModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

public class MediatorLiveDataActivity extends AppCompatActivity {

    @BindViewModel
    SeekBarViewModel mSeekBarViewModel;
    @BindViewModel
    TimerViewModel mTimerViewModel;

    @BindViewModel
    MediatorTestViewModel mediatorTestViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediator);
        Saber.bind(this);

        mediatorTestViewModel.addValueSource(mSeekBarViewModel.getValue(), new Observer<Integer>() {

            @Override
            public void onChanged(Integer integer) {
                mediatorTestViewModel.setValue(String.valueOf(integer));
            }
        });

        mediatorTestViewModel.addValueSource(mTimerViewModel.getTime(), new Observer<Long>() {

            @Override
            public void onChanged(Long aLong) {
                mediatorTestViewModel.setValue(String.valueOf(aLong));
            }

        });

        mTimerViewModel.setTime(900L);
        mSeekBarViewModel.setValue(120);
    }


    @OnChange(model = "mediatorTestViewModel")
    void setData(String value){
        // 输出 120 900 ，为倒序
        Log.d("MediatorActivity", value);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediatorTestViewModel.removeValueSource(mSeekBarViewModel.getValue());
        mediatorTestViewModel.removeValueSource(mTimerViewModel.getTime());
    }
}
