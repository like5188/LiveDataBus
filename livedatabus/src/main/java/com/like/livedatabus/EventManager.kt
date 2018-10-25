package com.like.livedatabus

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.os.Looper
import android.util.Log

object EventManager {
    private val eventList = mutableListOf<Event<*>>()

    fun isRegistered(host: Any) = eventList.any { it.host == host }

    fun <T> observe(host: Any, owner: LifecycleOwner, tag: String, requestCode: String, isSticky: Boolean, observer: Observer<T>) {
        val liveData = getLiveDataIfNullCreate<T>(tag, requestCode, isSticky)
        val busObserverWrapper = BusObserverWrapper(host, tag, requestCode, observer, liveData)
        val event = Event(host, owner, tag, requestCode, busObserverWrapper, liveData)
        // event由host、tag、requestCode组合决定
        if (eventList.contains(event)) {
            Log.e(LiveDataBus.TAG, "已经订阅过事件：$event")
            return
        }
        eventList.add(event)
        liveData.observe(owner, observer)
        Log.i(LiveDataBus.TAG, "订阅事件成功：$event")
        logHostOwnerEventDetails()
    }

    fun <T> post(tag: String, requestCode: String, t: T, isSticky: Boolean) {
        val requestCodeLogMessage = if (requestCode.isNotEmpty()) ", requestCode='$requestCode'" else ""
        val liveData = getLiveData<T>(tag, requestCode, isSticky)
        if (liveData != null) {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                Log.v(LiveDataBus.TAG, "在主线程发送消息 --> tag=$tag$requestCodeLogMessage，内容=$t")
                liveData.setValue(t)
            } else {
                Log.v(LiveDataBus.TAG, "在非主线程发送消息 --> tag=$tag$requestCodeLogMessage，内容=$t")
                liveData.postValue(t)
            }
        } else {
            Log.e(LiveDataBus.TAG, "发送消息失败，没有订阅事件： --> tag=$tag$requestCodeLogMessage")
        }
    }

    fun <T> removeObserver(observer: Observer<T>) {
        eventList.removeAll {
            it.observer == observer
        }
        Log.i(LiveDataBus.TAG, "取消事件：$observer")
        logHostOwnerEventDetails()
    }

    fun removeObservers(owner: LifecycleOwner) {
        eventList.removeAll { it.owner == owner }
        Log.i(LiveDataBus.TAG, "取消宿主：$owner")
        logHostOwnerEventDetails()
    }

    /**
     * LiveData由tag、requestCode组合决定
     */
    private fun <T> getLiveDataIfNullCreate(tag: String, requestCode: String, isSticky: Boolean): BusLiveData<T> {
        return getLiveData(tag, requestCode, isSticky) ?: BusLiveData()
    }

    /**
     * LiveData由tag、requestCode组合决定
     */
    private fun <T> getLiveData(tag: String, requestCode: String, isSticky: Boolean): BusLiveData<T>? {
        val filter = eventList.filter {
            it.tag == tag && it.requestCode == requestCode
        }
        return if (filter.isNotEmpty()) {
            val liveData = filter[0].liveData
            liveData.mNeedCurrentDataWhenFirstObserve = isSticky
            liveData as BusLiveData<T>
        } else {
            null
        }
    }

    /**
     * 打印缓存的事件、宿主、宿主所属生命周期类的详情
     */
    private fun logHostOwnerEventDetails() {
        val events = eventList.toSet()
        Log.d(LiveDataBus.TAG, "事件总数：${events.size}，包含：$events")

        val hosts = eventList.distinctBy { it.host }.map { it.host::class.java.simpleName }
        Log.d(LiveDataBus.TAG, "宿主总数：${hosts.size}，包含：$hosts")

        val owners = eventList.distinctBy { it.owner }.map { it.owner::class.java.simpleName }
        Log.d(LiveDataBus.TAG, "宿主所属生命周期类总数：${owners.size}，包含：$owners")
    }

}