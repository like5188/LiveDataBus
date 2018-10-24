package com.like.livedatabus

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.os.Looper
import android.util.Log

object EventManager {
    private val eventList = mutableListOf<Event<*>>()

    fun <T> observe(owner: LifecycleOwner, tag1: String, tag2: String, isSticky: Boolean, observer: Observer<T>) {
        val liveData = getLiveData<T>(tag1, tag2, isSticky) ?: BusLiveData()
        val observerWrapper = BusObserverWrapper(observer, liveData)
        val event = Event(owner, tag1, tag2, observerWrapper, liveData)
        if (eventList.contains(event)) {
            Log.e(LiveDataBus.TAG, "已经订阅过事件：$event")
            return
        }
        event.observe()
        eventList.add(event)
        Log.i(LiveDataBus.TAG, "订阅事件成功：$event，事件总数：${getEventCount()}，宿主总数：${getOwnerCount()}")
    }

    fun isRegistered(owner: LifecycleOwner): Boolean {
        val result = eventList.any { it.owner == owner }
        if (result) {
            Log.e(LiveDataBus.TAG, "已经注册过宿主：$owner")
        }
        return result
    }

    fun <T> post(tag1: String, tag2: String, t: T, isSticky: Boolean) {
        val liveData = getLiveData<T>(tag1, tag2, isSticky)
        if (liveData != null) {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                liveData.setValue(t)
            } else {
                liveData.postValue(t)
            }
            Log.d(LiveDataBus.TAG, "发送了消息 --> tag1=$tag1，tag2=$tag2，内容=$t")
        } else {
            Log.d(LiveDataBus.TAG, "发送消息失败，没有订阅事件： --> tag1=$tag1，tag2=$tag2")
        }
    }

    fun <T> removeObserver(observer: Observer<T>) {
        eventList.removeAll {
            Log.e(LiveDataBus.TAG, "${it.observer is BusObserverWrapper}")
            it.observer == observer
        }
        Log.i(LiveDataBus.TAG, "取消事件：$observer，剩余事件总数：${getEventCount()}")
    }

    fun removeObservers(owner: LifecycleOwner) {
        eventList.removeAll { it.owner == owner }
        Log.i(LiveDataBus.TAG, "取消宿主：$owner，剩余宿主总数：${getOwnerCount()}")
    }

    /**
     * LiveData由tag1、tag2组合决定
     */
    private fun <T> getLiveData(tag1: String, tag2: String, isSticky: Boolean): BusLiveData<T>? {
        val filter = eventList.filter {
            it.tag1 == tag1 && it.tag2 == tag2
        }
        return if (filter.isNotEmpty()) {
            val liveData = filter[0].liveData
            liveData!!.mNeedCurrentDataWhenFirstObserve = isSticky
            liveData as BusLiveData<T>
        } else {
            null
        }
    }

    /**
     * 注册的宿主总数
     */
    private fun getOwnerCount(): Int = eventList.distinctBy { it.owner }.size

    /**
     * 注册的事件总数
     */
    private fun getEventCount(): Int = eventList.toSet().size

}