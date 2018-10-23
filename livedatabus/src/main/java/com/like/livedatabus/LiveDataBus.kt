package com.like.livedatabus

import android.arch.lifecycle.MutableLiveData

class LiveDataBus private constructor() {
    companion object {
        @JvmStatic
        fun get(): LiveDataBus {
            return Holder.instance
        }
    }

    private object Holder {
        val instance = LiveDataBus()
    }

    private val bus = mutableMapOf<String, BusMutableLiveData<Any>>()

    fun <T> with(tag: String, type: Class<T>): MutableLiveData<T> {
        if (!bus.containsKey(tag)) {
            bus[tag] = BusMutableLiveData()
        }
        return bus[tag] as MutableLiveData<T>
    }

    fun with(tag: String): MutableLiveData<Any> {
        return with(tag, Any::class.java)
    }
}