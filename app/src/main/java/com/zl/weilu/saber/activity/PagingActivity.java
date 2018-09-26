package com.zl.weilu.saber.activity;

import android.os.Bundle;

import com.zl.weilu.saber.R;
import com.zl.weilu.saber.api.Saber;

import androidx.appcompat.app.AppCompatActivity;

public class PagingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paging);
        Saber.bind(this);
    }
   
}
