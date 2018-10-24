package com.like.livedatabus.sample

import android.util.Log
import com.like.livedatabus.registerLiveDataBus
import com.like.livedatabus_annotations.BusObserver

class MainViewModel(val activity: MainActivity) {

    init {
        registerLiveDataBus(activity)
    }

    @BusObserver(["like4"])
    fun test(i: Int) {
        Log.e("LiveDataBus", "MainViewModel onChanged tag1=like4，数据：$i")
    }

}