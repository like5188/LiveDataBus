package com.like.livedatabus.sample

import android.util.Log
import com.like.livedatabus.LiveDataBus
import com.like.livedatabus_annotations.BusObserver

class MainViewModel(activity: MainActivity) {

    init {
        LiveDataBus.register(this, activity)
    }

    @BusObserver(["like4"])
    fun test(i: Int) {
        Log.e("LiveDataBus", "MainViewModel test tag=like4，数据：$i")
    }

}