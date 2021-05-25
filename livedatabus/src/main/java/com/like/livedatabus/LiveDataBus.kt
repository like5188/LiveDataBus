package com.like.livedatabus

import android.util.Log
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.like.livedatabus.LiveDataBus.unregister

object LiveDataBus {
    /**
     * 注册宿主及其所属的生命周期类
     *
     * @param host  宿主，即调用此方法的类。注意：宿主类不能混淆，需要使用它的类名。
     * @param owner 宿主所属的生命周期类。
     * 如果是LifecycleOwner或者View类型，则不需要传递此参数，会自动获取它的LifecycleOwner。
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
        registerAllHierarchyFromOwner(host, owner, host.javaClass)
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
        EventManager.post(tag, "", NoObserverParams())
    }

    @JvmStatic
    fun <T> post(tag: String, t: T) {
        EventManager.post(tag, "", t)
    }

    @JvmStatic
    fun <T> post(tag: String, requestCode: String, t: T) {
        EventManager.post(tag, requestCode, t)
    }

    /**
     * 查找并实例化由 javapoet 自动生成的代理类。并调用它们的 autoGenerate 方法进行注册。
     *
     * @param host      宿主类
     * @param clazz     需要查找是否有对应的代理类（"${clazz.name}_Proxy"）的类
     */
    private fun registerAllHierarchyFromOwner(host: Any, owner: LifecycleOwner?, clazz: Class<*>?) {
        clazz ?: return
        Log.v(TAG, "registerAllHierarchyFromOwner --> $clazz")
        try {
            // 查找并实例化由javapoet自动生成的代理类，此类继承自Bridge类。
            Class.forName("${clazz.name}_Proxy")?.newInstance()?.apply {
                if (this is Bridge) {
                    autoGenerate(host, owner)
                }
            }
        } catch (e: Exception) {
        }
        // 继续查找父类。这里过滤开始的字符，及过滤android和java系统自带的类。
        clazz.superclass?.apply {
            if (
                !name.startsWith("android.") &&
                !name.startsWith("androidx.") &&
                !name.startsWith("java.") &&
                !name.startsWith("javax.")
            ) {
                registerAllHierarchyFromOwner(host, owner, this)
            }
        }
    }
}