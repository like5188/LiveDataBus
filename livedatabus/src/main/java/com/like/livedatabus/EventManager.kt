package com.like.livedatabus

import android.arch.lifecycle.LifecycleOwner
import android.os.Looper
import android.util.Log

class EventManager {
    private val eventList = mutableListOf<Event<*>>()

    fun <T> observe(event: Event<T>) {
        if (eventList.contains(event)) {
            Log.e(LiveDataBus.TAG, "已经订阅过事件：$event")
            return
        }
        event.observe()
        eventList.add(event)
    }

    fun isRegisteredHost(host: LifecycleOwner): Boolean {
        val result = eventList.any { it.host == host }
        if (result) {
            Log.e(LiveDataBus.TAG, "已经注册过宿主：$host")
        }
        return result
    }

    fun <T> postSticky(tag1: String, t: T) {
        postActual(tag1, "", t, true)
    }

    fun <T> postSticky(tag1: String, tag2: String, t: T) {
        postActual(tag1, tag2, t, true)
    }

    fun <T> post(tag1: String, t: T) {
        postActual(tag1, "", t, false)
    }

    fun <T> post(tag1: String, tag2: String, t: T) {
        postActual(tag1, tag2, t, false)
    }

    private fun <T> postActual(tag1: String, tag2: String, t: T, isSticky: Boolean) {
        val liveData = getLiveDataIfNullCreate<T>(tag1, tag2, isSticky)
        if (Looper.getMainLooper() == Looper.myLooper()) {
            liveData.setValue(t)
        } else {
            liveData.postValue(t)
        }
    }

    fun remove(host: LifecycleOwner) {
        eventList.listIterator().apply {
            this.forEach {
                if (it.host == host) {
                    this.remove()
                }
            }
        }
    }

    /**
     * LiveData由tag1、tag2组合决定
     */
    private fun <T> getLiveDataIfNullCreate(tag1: String, tag2: String, isSticky: Boolean): BusMutableLiveData<T> {
        val filter = eventList.filter {
            it.tag1 == tag1 && it.tag2 == tag2
        }
        return if (filter.isNotEmpty()) {
            filter[0].liveData
        } else {
            BusMutableLiveData()
        }.let {
            (it as BusMutableLiveData<T>).mNeedCurrentDataWhenFirstObserve = isSticky
        }
    }

    /**
     * 注册的宿主总数
     */
    private fun getHostCount(): Int {
        val result = mutableSetOf<LifecycleOwner>()
        eventList.forEach {
            result.add(it.host)
        }
        return result.size
    }

    /**
     * 注册的事件总数
     */
    private fun getEventCount(): Int = eventList.toSet().size

}