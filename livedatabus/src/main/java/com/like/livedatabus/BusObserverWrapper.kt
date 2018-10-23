package com.like.livedatabus

import android.arch.lifecycle.Observer

class BusObserverWrapper<T>(private val observer: Observer<T>, private val liveData: BusLiveData<T>) : Observer<T> {

    override fun onChanged(t: T?) {
        if (liveData.mSetValue) {
            observer.onChanged(t)
        }
    }
}