package com.zl.weilu.saber.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.zl.weilu.saber.R;
import com.zl.weilu.saber.annotation.BindViewModel;
import com.zl.weilu.saber.annotation.OnChange;
import com.zl.weilu.saber.api.Saber;
import com.zl.weilu.saber.viewmodel.LiveDataTimerViewModel;


public class MainActivity extends AppCompatActivity {

    private TextView textView;

    @BindViewModel
    LiveDataTimerViewModel mTimerViewModel;
    
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
        Saber.bind(this);
    }

    @OnChange(model = "mTimerViewModel")
    void setData(Long time){
        String newText = MainActivity.this.getResources().getString(R.string.seconds, time);
        textView.setText(newText);
        Log.d("MainActivity", "Updating timer");
    }
}
