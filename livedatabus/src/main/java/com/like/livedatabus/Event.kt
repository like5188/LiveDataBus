package com.like.livedatabus

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer

class Event<T>(
        val host: LifecycleOwner,
        val tag1: String,
        val tag2: String,
        val isSticky: Boolean,
        val observer: Observer<T>,
        val liveData: BusMutableLiveData<T>
) {

    fun observe() {
        liveData.observe(host, observer)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Event<*>

        if (host != other.host) return false
        if (tag1 != other.tag1) return false
        if (tag2 != other.tag2) return false

        return true
    }

    override fun hashCode(): Int {
        var result = host.hashCode()
        result = 31 * result + tag1.hashCode()
        result = 31 * result + tag2.hashCode()
        return result
    }
}