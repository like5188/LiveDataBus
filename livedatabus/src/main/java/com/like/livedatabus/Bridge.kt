package com.like.livedatabus

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

/**
 * 连接 LiveDataBus 和 javapoet 自动生成的代码 的桥梁
 */
abstract class Bridge {

    /**
     * 自动生成的代码中重写此方法。
     * 方法体是对 host 中所有注册的 tag 进行 [observe] 方法的调用
     */
    abstract fun autoGenerate(host: Any, owner: LifecycleOwner?)

    /**
     * 在代理类中重写 [autoGenerate] 方法，然后调用此方法进行注册
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