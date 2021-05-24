package com.like.livedatabus

import android.util.Log
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.like.livedatabus.LiveDataBus.unregister

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
    fun register(
        host: Any,
        owner: LifecycleOwner? = when (host) {
            is LifecycleOwner -> host
            is View -> host.findViewTreeLifecycleOwner()
            else -> null
        }
    ) {
        if (EventManager.isRegistered(host)) {
            Log.e(TAG, "已经注册过宿主：$host")
            return
        }
        Log.i(TAG, "注册宿主：$host")
        mBridge.register(host, owner)
    }

    /**
     * 取消注册的宿主
     */
    @JvmStatic
    fun unregister(host: Any) {
        EventManager.removeHost(host)
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