package com.like.livedatabus

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.like.livedatabus.LiveDataBus.unregister

fun Any.liveDataBusRegister(owner: LifecycleOwner? = null) {
    LiveDataBus.register(this, owner)
}

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
     * @param host  宿主。注意：宿主类不能混淆。
     * @param owner 宿主所属的生命周期类。
     * 如果为 null，则会使用 liveData.observeForever(observer) 进行注册。那么就需要调用 [unregister] 方法取消注册。
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
     * 取消注册宿主及其所属的生命周期类
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