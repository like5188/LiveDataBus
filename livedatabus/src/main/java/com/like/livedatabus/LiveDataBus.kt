package com.like.livedatabus

import androidx.lifecycle.LifecycleOwner
import android.util.Log

/**
 * 注册宿主及其所属的生命周期类
 *
 * @param owner 宿主所属的生命周期类
 */
fun Any.liveDataBusRegister(owner: LifecycleOwner? = null) {
    LiveDataBus.register(this, owner)
}

/**
 * 取消注册宿主及其所属的生命周期类
 */
fun Any.liveDataBusUnRegister(tag: String, requestCode: String = "") {
    LiveDataBus.unregister(tag, requestCode)
}

object LiveDataBus {
    const val TAG = "LiveDataBus"
    private val mNoObserverParams = NoObserverParams()
    private val mBridge = Bridge()

    /**
     * 注册宿主及其所属的生命周期类
     *
     * @param host 宿主
     * @param owner 宿主所属的生命周期类
     */
    @JvmStatic
    @JvmOverloads
    fun register(host: Any, owner: LifecycleOwner? = null) {
        if (EventManager.isRegistered(host)) {
            Log.e(TAG, "已经注册过宿主：$host")
            return
        }
        Log.i(TAG, "注册宿主：$host")
        mBridge.register(host, owner)
    }

    /**
     * 手动移除观察者
     */
    @JvmStatic
    @JvmOverloads
    fun unregister(tag: String, requestCode: String = "") {
        EventManager.removeObserver(tag, requestCode)
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