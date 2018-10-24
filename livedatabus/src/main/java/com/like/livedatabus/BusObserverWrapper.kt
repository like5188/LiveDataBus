package com.like.livedatabus

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.util.Log

class BusObserverWrapper<T>(private val owner: LifecycleOwner,
                            private val tag1: String,
                            private val tag2: String,
                            private val observer: Observer<T>,
                            private val liveData: BusLiveData<T>)
    : Observer<T> {

    override fun onChanged(t: T?) {
        if (liveData.mSetValue) {
            try {
                observer.onChanged(t)
            } catch (e: Exception) {
                Log.e(LiveDataBus.TAG, "发送消息失败：发送的数据类型和接收的数据类型不一致。owner=$owner，tag1=$tag1，tag2=$tag2")
            }
        }
    }
}