package com.like.livedatabus.sample;

import android.arch.lifecycle.Observer;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.like.livedatabus.LiveDataBus;
import com.like.livedatabus.sample.databinding.ActivitySecondBinding;

public class SecondActivity extends AppCompatActivity {
    private ActivitySecondBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_second);
    }

    public void register1(View view) {
        LiveDataBus.with("like1", Integer.class).observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                Log.e("LiveDataBus", "SecondActivity onChanged");
                mBinding.tv1.setText(integer == null ? "" : integer.toString());
            }
        });
    }

    public void changeData1(View view) {
        int oldValue = mBinding.tv1.getText().toString().isEmpty() ? 0 : Integer.parseInt(mBinding.tv1.getText().toString());
        int newValue = oldValue + 1;
        LiveDataBus.with("like1", Integer.class).setValue(newValue);
    }

    public void register2(View view) {
        LiveDataBus.with("like2", Integer.class).observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                Log.e("LiveDataBus", "SecondActivity onChanged");
                mBinding.tv2.setText(integer == null ? "" : integer.toString());
            }
        });
    }

    public void changeData2(View view) {
        int oldValue = mBinding.tv2.getText().toString().isEmpty() ? 0 : Integer.parseInt(mBinding.tv2.getText().toString());
        int newValue = oldValue + 1;
        LiveDataBus.with("like2", Integer.class).setValue(newValue);
    }

}