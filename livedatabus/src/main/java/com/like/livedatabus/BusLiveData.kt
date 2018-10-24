package com.like.livedatabus

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer

class BusLiveData<T> : MutableLiveData<T>() {
    // 首次注册的时候，是否需要当前LiveData的最新数据
    internal var mNeedCurrentDataWhenFirstObserve = false
    // 主动触发数据更新事件才通知所有Observer
    internal var mSetValue = false

    override fun setValue(value: T) {
        mSetValue = true
        super.setValue(value)
    }

    override fun postValue(value: T) {
        mSetValue = true
        super.postValue(value)
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<T>) {
        //mSetValue 可过滤掉liveData首次创建时监听之前的遗留的值
        mSetValue = mNeedCurrentDataWhenFirstObserve
        super.observe(owner, observer)
    }

    override fun removeObserver(observer: Observer<T>) {
        super.removeObserver(observer)
        EventManager.removeObserver(observer)
    }

    override fun removeObservers(owner: LifecycleOwner) {
        super.removeObservers(owner)
        EventManager.removeObservers(owner)
    }

}