package com.like.livedatabus.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.like.livedatabus.LiveDataBus
import com.like.livedatabus_annotations.BusObserver

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LiveDataBus.register(this)
    }

    @BusObserver(["like3"])
    fun test(i: Int) {
        Log.e("LiveDataBus", "BaseActivity onChanged tag1=like3，数据：$i")
    }
}