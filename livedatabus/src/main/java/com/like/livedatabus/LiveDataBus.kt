package com.like.livedatabus

import android.arch.lifecycle.LifecycleOwner

object LiveDataBus {
    val TAG = LiveDataBus::class.java.simpleName
    private val mBridge = Bridge()

    fun register(owner: LifecycleOwner) {
        if (!EventManager.isRegistered(owner)) {
            mBridge.register(owner)
        }
    }

    fun <T> post(tag1: String, t: T) {
        EventManager.post(tag1, "", t, false)
    }

    fun <T> post(tag1: String, tag2: String, t: T) {
        EventManager.post(tag1, tag2, t, false)
    }

    fun <T> postSticky(tag1: String, t: T) {
        EventManager.post(tag1, "", t, true)
    }

    fun <T> postSticky(tag1: String, tag2: String, t: T) {
        EventManager.post(tag1, tag2, t, true)
    }
}