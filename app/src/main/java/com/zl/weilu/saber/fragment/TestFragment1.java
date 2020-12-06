package com.zl.weilu.saber.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.zl.weilu.saber.R;
import com.zl.weilu.saber.annotation.BindViewModel;
import com.zl.weilu.saber.annotation.OnChange;
import com.zl.weilu.saber.api.Saber;
import com.zl.weilu.saber.viewmodel.SeekBarViewModel;

/**
 * Shows a SeekBar that is synced with a value in a ViewModel.
 */
public class TestFragment1 extends Fragment {

    private SeekBar mSeekBar;
    private TextView mTextView;
    /**
     * 指定key值
     */
    @BindViewModel(key = "mm", isShare = true)
    SeekBarViewModel mViewModel;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_test, container, false);
        mSeekBar = root.findViewById(R.id.seekBar);
        mTextView = root.findViewById(R.id.tv);
        Saber.bind(this);
        subscribeSeekBar();
        return root;
    }

    private void subscribeSeekBar() {

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mViewModel.setValue1(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }

    @OnChange
    void setData(Integer value1){
        Log.d("TestFragment1", value1 + "");
        if (value1 != null) {
            mTextView.setText(value1 + "%");
            mSeekBar.setProgress(value1);
        }
    }
}
