package com.zl.weilu.saber;

import android.app.Application;

import com.jeremyliao.liveeventbus.LiveEventBus;

/**
 * @Description:
 * @Author: weilu
 * @Time: 2019/5/16 0016 09:28.
 */
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LiveEventBus.config()
                .enableLogger(BuildConfig.DEBUG)
                .lifecycleObserverAlwaysActive(false);
    }
}
