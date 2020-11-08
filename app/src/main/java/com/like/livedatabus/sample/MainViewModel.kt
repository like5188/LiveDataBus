package com.like.livedatabus.sample

import android.util.Log
import com.like.livedatabus.liveDataBusRegisterByUniqueInstance
import com.like.livedatabus_annotations.BusObserver

class MainViewModel(private val activity: MainActivity) {

    init {
        liveDataBusRegisterByUniqueInstance(activity)
    }

    @BusObserver(["like4"])
    fun test(i: Int) {
        Log.e("LiveDataBus", "MainViewModel test tag=like4，数据：$i")
    }

}