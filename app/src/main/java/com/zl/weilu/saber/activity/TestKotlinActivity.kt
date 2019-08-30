package com.zl.weilu.saber.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.zl.weilu.saber.R
import com.zl.weilu.saber.annotation.BindViewModel
import com.zl.weilu.saber.annotation.OnChange
import com.zl.weilu.saber.api.Saber
import com.zl.weilu.saber.viewmodel.KotlinBeanViewModel

class TestKotlinActivity : AppCompatActivity() {

    @JvmField
    @BindViewModel
    var mViewModel: KotlinBeanViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        Saber.bind(this)
        mViewModel?.setB(false)
    }

    @OnChange
    fun onChange(b: Boolean?) {
        Log.e("TestKotlinActivity:", "$b")
    }
}
