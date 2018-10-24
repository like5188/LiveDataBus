package com.like.livedatabus.sample;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.like.livedatabus.LiveDataBus;
import com.like.livedatabus.sample.databinding.ActivitySecondBinding;
import com.like.livedatabus_annotations.BusObserver;

public class SecondActivity extends AppCompatActivity {
    private ActivitySecondBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_second);
    }

    public void changeData1(View view) {
        LiveDataBus.INSTANCE.post("like1", 1000L);
    }

    public void changeData2(View view) {
        LiveDataBus.INSTANCE.post("like2", new User("name", 18));
    }

    @BusObserver("like1")
    public void observer1(long l) {
        Log.e("LiveDataBus", "SecondActivity onChanged tag1=like1");
        mBinding.tv1.setText(String.valueOf(l));
    }

    @BusObserver("like2")
    public void observer2(User u) {
        Log.e("LiveDataBus", "SecondActivity onChanged tag1=like2");
        mBinding.tv2.setText(u.toString());
    }

}