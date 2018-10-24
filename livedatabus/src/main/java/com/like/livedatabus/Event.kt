package com.like.livedatabus

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer

class Event<T>(
        val owner: LifecycleOwner,
        val tag1: String,
        val tag2: String,
        private val observer: Observer<T>
) {
    var liveData: BusLiveData<T>? = null

    fun observe() {
        liveData?.observe(owner, observer)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Event<*>

        if (owner != other.owner) return false
        if (tag1 != other.tag1) return false
        if (tag2 != other.tag2) return false

        return true
    }

    override fun hashCode(): Int {
        var result = owner.hashCode()
        result = 31 * result + tag1.hashCode()
        result = 31 * result + tag2.hashCode()
        return result
    }

    override fun toString(): String {
        return "Event(owner=$owner, tag1='$tag1', tag2='$tag2')"
    }

}