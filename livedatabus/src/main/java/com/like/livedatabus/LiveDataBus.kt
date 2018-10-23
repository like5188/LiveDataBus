package com.like.livedatabus

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

    fun <T> with(tag: String, type: Class<T>): BusMutableLiveData<T> {
        if (!bus.containsKey(tag)) {
            bus[tag] = BusMutableLiveData()
        }
        return bus[tag] as BusMutableLiveData<T>
    }

    fun with(tag: String): BusMutableLiveData<Any> {
        return with(tag, Any::class.java)
    }
}