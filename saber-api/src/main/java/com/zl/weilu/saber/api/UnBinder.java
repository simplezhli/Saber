package com.zl.weilu.saber.api;
import android.support.annotation.UiThread;

/**
 * @author weilu
 * @date 2018/11/23 0023 17:48.
 */
public interface UnBinder {
    
    @UiThread
    void unbind();

    UnBinder EMPTY = new UnBinder() {
        @Override
        public void unbind() {

        }
    };
}
