package com.like.livedatabus

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer

class BusMutableLiveData<T> : MutableLiveData<T>() {
    private val observerMap = mutableMapOf<Observer<T>, Observer<T>>()

    /**
     * 如果注册时，希望接收到LiveData中已经存在的最新数据。
     * 生命周期感知，不需要手动取消订阅
     */
    fun observeSticky(owner: LifecycleOwner, observer: Observer<T>) {
        super.observe(owner, observer)
    }

    /**
     * 如果注册时，希望接收到LiveData中已经存在的最新数据。
     * 需要手动取消订阅
     */
    fun observeForeverSticky(observer: Observer<T>) {
        super.observeForever(observer)
    }

    /**
     * 生命周期感知，不需要手动取消订阅
     */
    override fun observe(owner: LifecycleOwner, observer: Observer<T>) {
        super.observe(owner, observer)
        try {
            hook(observer)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 需要手动取消订阅
     */
    override fun observeForever(observer: Observer<T>) {
        if (!observerMap.containsKey(observer)) {
            observerMap[observer] = BusObserverWrapper(observer)
        }
        super.observeForever(observerMap[observer]!!)
    }

    override fun removeObserver(observer: Observer<T>) {
        val realObserver: Observer<T> =
                if (observerMap.containsKey(observer)) {
                    observerMap.remove(observer)!!
                } else {
                    observer
                }
        super.removeObserver(realObserver)
    }

    /**
     * 使用observe方法注册时，会接收到LiveData中已经存在的最新数据。
     *
     * 解决办法：把ObserverWrapper的版本号设置成mLastVersion和mVersion一样。
     */
    @Throws(Exception::class)
    private fun hook(observer: Observer<T>) {
        // 获取LiveData类中的mObservers对象
        val classLiveData = LiveData::class.java
        val fieldObservers = classLiveData.getDeclaredField("mObservers")
        fieldObservers.isAccessible = true
        val mObservers = fieldObservers.get(this)
        // 获取mObservers对象的类型SafeIterableMap
        val classObservers = mObservers::class.java
        // 获取SafeIterableMap的get方法
        val methodGet = classObservers.getDeclaredMethod("get", Any::class.java)
        methodGet.isAccessible = true
        // 执行get方法，获取Map.Entry<K, V>对象
        val observerEntry = methodGet.invoke(mObservers, observer)
        // 获取Map.Entry<K, V>中的value对象LifecycleBoundObserver
        val lifecycleBoundObserver = if (observerEntry is Map.Entry<*, *>) {
            observerEntry.value
        } else {
            throw NullPointerException("BusObserverWrapper can not be null!")
        }
        // 获取LifecycleBoundObserver的父类LiveData.ObserverWrapper类
        val classObserverWrapper = lifecycleBoundObserver!!::class.java.superclass!!
        // 获取ObserverWrapper的版本号mLastVersion
        val filedLastVersion = classObserverWrapper.getDeclaredField("mLastVersion")
        filedLastVersion.isAccessible = true
        // 获取LiveData的版本号mVersion
        val fieldVersion = classLiveData.getDeclaredField("mVersion")
        fieldVersion.isAccessible = true
        val mVersion = fieldVersion.get(this)
        // 把ObserverWrapper的版本号mLastVersion设置成和mVersion一样
        filedLastVersion.set(lifecycleBoundObserver, mVersion)
    }
}