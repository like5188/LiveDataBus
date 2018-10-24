package com.like.livedatabus

import android.arch.lifecycle.LifecycleOwner

/**
 * 如果owner和host一样时调用
 */
fun LifecycleOwner.registerLiveDataBus() {
    LiveDataBus.register(this)
}

/**
 * 如果owner和host不一样时调用
 */
fun Any.registerLiveDataBus(owner: LifecycleOwner) {
    LiveDataBus.register(owner, this)
}

object LiveDataBus {
    const val TAG = "LiveDataBus"
    private val mBridge = Bridge()

    /**
     * 如果owner和host一样时调用
     */
    @JvmStatic
    fun register(owner: LifecycleOwner) {
        register(owner, owner)
    }

    /**
     * 如果owner和host不一样时调用
     */
    @JvmStatic
    fun register(owner: LifecycleOwner, host: Any) {
        if (!EventManager.isRegistered(owner)) {
            mBridge.register(owner, host)
        }
    }

    @JvmStatic
    fun <T> post(tag1: String, t: T) {
        EventManager.post(tag1, "", t, false)
    }

    @JvmStatic
    fun <T> post(tag1: String, tag2: String, t: T) {
        EventManager.post(tag1, tag2, t, false)
    }

    @JvmStatic
    fun <T> postSticky(tag1: String, t: T) {
        EventManager.post(tag1, "", t, true)
    }

    @JvmStatic
    fun <T> postSticky(tag1: String, tag2: String, t: T) {
        EventManager.post(tag1, tag2, t, true)
    }
}