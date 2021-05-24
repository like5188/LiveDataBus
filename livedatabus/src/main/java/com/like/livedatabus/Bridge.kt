package com.like.livedatabus

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

/**
 * 连接LiveDataBus和自动生成的代码的桥梁
 */
open class Bridge {
    /**
     * 注册宿主及其父类
     */
    fun register(host: Any, owner: LifecycleOwner? = null) {
        try {
            registerAllHierarchyFromOwner(host, owner, host.javaClass)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 初始化宿主及其父类，及调用它们的autoGenerate方法，避免父类中的tag未注册
     *
     * @param clazz 宿主的类
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun registerAllHierarchyFromOwner(host: Any, owner: LifecycleOwner?, clazz: Class<*>?) {
        clazz ?: return
        Log.v(LiveDataBus.TAG, "registerAllHierarchyFromOwner --> $clazz")
        // 查找自动生成的代理类，此类继承Bridge类
        var proxyClass: Class<*>? = null
        try {
            proxyClass = Class.forName("${clazz.name}_Proxy")
        } catch (e: Exception) {
        }
        // 初始化
        proxyClass?.newInstance().let {
            if (it is Bridge) {
                it.autoGenerate(host, owner)
            }
        }
        // 继续查找并初始化父类宿主。这里过滤开始的字符，及过滤android和java系统自带的类。
        val superClass = clazz.superclass
        if (superClass != null
            && !superClass.name.startsWith("android.")
            && !superClass.name.startsWith("androidx.")
            && !superClass.name.startsWith("java.")
            && !superClass.name.startsWith("javax.")
        ) {
            registerAllHierarchyFromOwner(host, owner, superClass)
        }
    }

    /**
     * 自动生成代码时重写此方法，方法体是对host中所有注册的tag进行observe()方法的调用
     */
    protected open fun autoGenerate(host: Any, owner: LifecycleOwner?) {
    }

    /**
     * 在代理类中重写autoGenerate方法，然后调用此方法进行注册
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