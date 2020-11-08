package com.like.livedatabus

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.like.livedatabus.LiveDataBus.unregister

fun Any.liveDataBusRegisterByUniqueInstance(owner: LifecycleOwner? = null) {
    LiveDataBus.registerByUniqueInstance(this, owner)
}

fun Any.liveDataBusRegisterByUniqueClass(owner: LifecycleOwner? = null) {
    LiveDataBus.registerByUniqueClass(this, owner)
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
     * 根据宿主实例来判断是否重复注册。
     *
     * @param host  宿主。注意：宿主类不能混淆。
     * @param owner 宿主所属的生命周期类。
     * 如果为 null，则会使用 liveData.observeForever(observer) 进行注册。那么就需要调用 [unregister] 方法取消注册。
     */
    fun registerByUniqueInstance(host: Any, owner: LifecycleOwner? = null) {
        if (EventManager.containsHostInstance(host)) {
            Log.e(TAG, "已经注册过宿主实例：$host")
            return
        }
        Log.i(TAG, "注册宿主：$host")
        mBridge.register(host, owner)
    }

    /**
     * 注册宿主及其所属的生命周期类
     * 根据宿主类型来判断是否重复注册。
     * 当[owner]为 null 时，退出应用后，由于没有[unregister]，且没有杀死进程，所以该宿主还存在，再次打开应用会造成重复注册。
     * 比如：下载时，要求退出应用还能继续下载的情况。参考 [com.github.like5188:Update] 库。此时就应该采用此方法注册。
     *
     * @param host  宿主。注意：宿主类不能混淆。
     * @param owner 宿主所属的生命周期类。
     * 如果为 null，则会使用 liveData.observeForever(observer) 进行注册。那么就需要调用 [unregister] 方法取消注册。
     */
    fun registerByUniqueClass(host: Any, owner: LifecycleOwner? = null) {
        if (EventManager.containsHostClass(host.javaClass)) {
            Log.e(TAG, "已经注册过宿主类：${host.javaClass}")
            return
        }
        Log.i(TAG, "注册宿主：$host")
        mBridge.register(host, owner)
    }

    /**
     * 取消注册宿主及其所属的生命周期类
     */
    fun unregister(tag: String, requestCode: String = "") {
        EventManager.removeObserver(tag, requestCode)
    }

    fun post(tag: String) {
        EventManager.post(tag, "", mNoObserverParams)
    }

    fun <T> post(tag: String, t: T) {
        EventManager.post(tag, "", t)
    }

    fun <T> post(tag: String, requestCode: String, t: T) {
        EventManager.post(tag, requestCode, t)
    }

}