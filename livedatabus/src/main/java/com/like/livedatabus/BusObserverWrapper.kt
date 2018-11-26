package com.like.livedatabus

import android.arch.lifecycle.Observer
import android.util.Log

class BusObserverWrapper<T>(val host: Any,
                            val tag: String,
                            val requestCode: String,
                            private val observer: Observer<T>,
                            private val liveData: BusLiveData<T>)
    : Observer<T> {

    override fun onChanged(t: T?) {
        if (liveData.mSetValue) {
            try {
                observer.onChanged(t)
            } catch (e: Exception) {
                Log.e(LiveDataBus.TAG, "发送消息失败：发送的数据类型和接收的数据类型不一致。host=$host，tag=$tag，requestCode=$requestCode Exception=$e")
            }
        }
    }
}