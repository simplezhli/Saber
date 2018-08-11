package com.zl.weilu.saber.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.zl.weilu.saber.R;
import com.zl.weilu.saber.annotation.BindViewModel;
import com.zl.weilu.saber.annotation.OnChange;
import com.zl.weilu.saber.api.Saber;
import com.zl.weilu.saber.viewmodel.SeekBarViewModel;

import androidx.fragment.app.Fragment;

/**
 * Shows a SeekBar that is synced with a value in a ViewModel.
 */
public class TestFragment2 extends Fragment {

    private SeekBar mSeekBar;

    /**
     * 指定key值
     */
    @BindViewModel(key = "mm", isShare = true)
    SeekBarViewModel mSeekBarViewModel;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_test, container, false);
        mSeekBar = root.findViewById(R.id.seekBar);
        Saber.bind(this);
        subscribeSeekBar();
        return root;
    }

    private void subscribeSeekBar() {

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mSeekBarViewModel.setValue(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }

    @OnChange(model = "mSeekBarViewModel")
    void setData(Integer value){
        if (value != null) {
            mSeekBar.setProgress(value);
        }
    }
}
