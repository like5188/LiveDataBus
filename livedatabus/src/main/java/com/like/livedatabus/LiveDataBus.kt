package com.like.livedatabus

class LiveDataBus private constructor() {
    companion object {
        fun <T> with(tag: String): BusMutableLiveData<T> = Holder.instance.with(tag, false)

        // 兼容java
        @JvmStatic
        fun <T> with(tag: String, clazz: Class<T>): BusMutableLiveData<T> = Holder.instance.with(tag, false)

        fun <T> withSticky(tag: String): BusMutableLiveData<T> = Holder.instance.with(tag, true)

        // 兼容java
        @JvmStatic
        fun <T> withSticky(tag: String, clazz: Class<T>): BusMutableLiveData<T> = Holder.instance.with(tag, true)
    }

    private object Holder {
        val instance = LiveDataBus()
    }

    private val bus = mutableMapOf<String, BusMutableLiveData<Any>>()

    private fun <T> with(tag: String, isSticky: Boolean): BusMutableLiveData<T> {
        if (!bus.containsKey(tag)) {
            bus[tag] = BusMutableLiveData()
        }
        return bus[tag]!!.let {
            it.mNeedCurrentDataWhenFirstObserve = isSticky
            it as BusMutableLiveData<T>
        }
    }

}