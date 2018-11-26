package com.like.livedatabus

import android.arch.lifecycle.LifecycleOwner
import android.util.Log
import java.lang.IllegalArgumentException

/**
 * 注册宿主及其所属的生命周期类
 *
 * 注意：用此方法注册，调用者必须实现LifecycleOwner接口。
 * 否则请调用{@link liveDataBusRegister(owner: LifecycleOwner)}方法进行注册。
 */
fun Any.liveDataBusRegister() {
    LiveDataBus.register(this)
}

/**
 * 注册宿主及其所属的生命周期类
 *
 * @param owner 宿主所属的生命周期类
 */
fun Any.liveDataBusRegister(owner: LifecycleOwner) {
    LiveDataBus.register(this, owner)
}

object LiveDataBus {
    const val TAG = "LiveDataBus"
    private val mNoObserverParams = NoObserverParams()
    private val mBridge = Bridge()

    /**
     * 注册宿主及其所属的生命周期类
     *
     * 注意：用此方法注册，host必须实现LifecycleOwner接口。
     * 否则请调用{@link LiveDataBus#register(host: Any, owner: LifecycleOwner)}方法进行注册。
     *
     * @param host 宿主
     */
    @JvmStatic
    fun register(host: Any) {
        if (host is LifecycleOwner) {
            register(host, host)
        } else {
            throw IllegalArgumentException("host没有实现LifecycleOwner接口，请调用register(host: Any, owner: LifecycleOwner)方法进行注册")
        }
    }

    /**
     * 注册宿主及其所属的生命周期类
     *
     * @param host 宿主
     * @param owner 宿主所属的生命周期类
     */
    @JvmStatic
    fun register(host: Any, owner: LifecycleOwner) {
        if (EventManager.isRegistered(host)) {
            Log.e(LiveDataBus.TAG, "已经注册过宿主：$host")
            return
        }
        Log.i(LiveDataBus.TAG, "注册宿主：$host")
        mBridge.register(host, owner)
    }

    @JvmStatic
    fun post(tag: String) {
        EventManager.post(tag, "", mNoObserverParams)
    }

    @JvmStatic
    fun <T> post(tag: String, t: T) {
        EventManager.post(tag, "", t)
    }

    @JvmStatic
    fun <T> post(tag: String, requestCode: String, t: T) {
        EventManager.post(tag, requestCode, t)
    }

}