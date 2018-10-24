package com.like.livedatabus

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.util.Log
import java.lang.Exception

/**
 * 连接LiveDataBus和自动生成的代码的桥梁
 */
open class Bridge {
    /**
     * 注册宿主及其父类
     */
    fun register(owner: LifecycleOwner, host: Any) {
        try {
            registerAllHierarchyFromOwner(owner, host, host.javaClass)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 初始化宿主及其父类，及调用它们的autoGenerate方法，避免父类中的tag未注册
     *
     * @param clazz
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun registerAllHierarchyFromOwner(owner: LifecycleOwner, host: Any, clazz: Class<*>?) {
        clazz ?: return
        Log.d(LiveDataBus.TAG, "registerAllHierarchyFromOwner --> $clazz")
        // 查找自动生成的代理类，此类继承Bridge类
        var proxyClass: Class<*>? = null
        try {
            proxyClass = Class.forName("${clazz.name}_Proxy")
        } catch (e: Exception) {
        }
        // 初始化
        proxyClass?.newInstance().let {
            if (it is Bridge) {
                it.autoGenerate(owner, host)
            }
        }
        // 继续查找并初始化父类。这里过滤开始的字符，及过滤android和java系统自带的类。
        val superClass = clazz.superclass
        if (superClass != null
                && !superClass.name.startsWith("android.")
                && !superClass.name.startsWith("java.")) {
            registerAllHierarchyFromOwner(owner, host, superClass)
        }
    }

    /**
     * 自动生成代码时重写此方法，方法体是对entity中所有注册的tag进行observe()方法的调用
     */
    protected open fun autoGenerate(owner: LifecycleOwner, host: Any) {
    }

    /**
     * 在代理类中重写autoGenerate方法，然后调用observe方法进行注册
     */
    protected fun <T> observe(owner: LifecycleOwner, tag1: String, tag2: String, isSticky: Boolean, observer: Observer<T>) {
        if (tag1.isEmpty()) {
            return
        }
        EventManager.observe(owner, tag1, tag2, isSticky, observer)
    }
}