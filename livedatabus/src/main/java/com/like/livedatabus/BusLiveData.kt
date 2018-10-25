package com.like.livedatabus

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer

class BusLiveData<T> : MutableLiveData<T>() {
    /**
     * 主动触发数据更新事件才通知所有Observer，忽略用observe方法注册时引起的改变。
     * 即当mSetValue为true时。则会在注册的时候就收到之前发送的最新一条消息。当为false时，则不会收到消息。
     */
    internal var mSetValue = false

    override fun setValue(value: T) {
        mSetValue = true
        super.setValue(value)
    }

    override fun postValue(value: T) {
        mSetValue = true
        super.postValue(value)
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