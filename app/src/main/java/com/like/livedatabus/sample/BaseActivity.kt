package com.like.livedatabus.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import com.like.livedatabus.liveDataBusRegister
import com.like.livedatabus_annotations.BusObserver

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        liveDataBusRegister()
    }

    @BusObserver(["like3"])
    fun test(i: Int) {
        Log.e("LiveDataBus", "BaseActivity test tag=like3，数据：$i")
    }
}