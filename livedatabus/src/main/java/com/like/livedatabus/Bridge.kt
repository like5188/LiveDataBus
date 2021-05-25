package com.like.livedatabus

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

/**
 * 连接 LiveDataBus 和 javapoet 自动生成的代码 的桥梁
 */
open class Bridge {
    /**
     * 注册宿主及其父类
     */
    fun register(host: Any, owner: LifecycleOwner? = null) {
        registerAllHierarchyFromOwner(host, owner, host.javaClass)
    }

    /**
     * 查找并实例化由 javapoet 自动生成的代理类。并调用它们的[autoGenerate]方法。
     *
     * @param host      宿主类
     * @param clazz     需要查找是否有对应的代理类（"${clazz.name}_Proxy"）的类
     */
    private fun registerAllHierarchyFromOwner(host: Any, owner: LifecycleOwner?, clazz: Class<*>?) {
        clazz ?: return
        Log.v(LiveDataBus.TAG, "registerAllHierarchyFromOwner --> $clazz")
        try {
            // 查找并实例化由javapoet自动生成的代理类，此类继承自Bridge类。
            Class.forName("${clazz.name}_Proxy")?.newInstance()?.apply {
                if (this is Bridge) {
                    this.autoGenerate(host, owner)
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

    /**
     * 自动生成的代码中重写此方法。
     * 方法体是对 host 中所有注册的 tag 进行 observe() 方法的调用
     */
    protected open fun autoGenerate(host: Any, owner: LifecycleOwner?) {
    }

    /**
     * 在代理类中重写 autoGenerate 方法，然后调用此方法进行注册
     */
    protected fun <T> observe(
        host: Any?,
        owner: LifecycleOwner?,
        tag: String?,
        requestCode: String?,
        isSticky: Boolean?,
        observer: Observer<T>?
    ) {
        host ?: return
        tag ?: return
        requestCode ?: return
        isSticky ?: return
        observer ?: return
        if (tag.isEmpty()) {
            return
        }
        EventManager.observe(host, owner, tag, requestCode, isSticky, observer)
    }
}