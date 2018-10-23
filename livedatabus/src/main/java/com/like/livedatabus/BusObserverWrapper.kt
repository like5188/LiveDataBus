package com.like.livedatabus

import android.arch.lifecycle.Observer

/**
 * 使用observeForever方法注册时，会接收到LiveData中已经存在的最新数据。
 *
 * 解决办法：既然是在调用内回调的，那么我们可以写一个ObserverWrapper，把真正的回调给包装起来。
 * 把ObserverWrapper传给observeForever，那么在回调的时候我们去检查调用栈，
 * 如果回调是observeForever方法引起的，那么就不回调真正的订阅者。
 */
class BusObserverWrapper<T>(private val observer: Observer<T>? = null) : Observer<T> {

    override fun onChanged(t: T?) {
        if (observer != null) {
            if (isCallOnObserveForever()) {
                return
            }
            observer.onChanged(t)
        }
    }

    private fun isCallOnObserveForever(): Boolean {
        Thread.currentThread().stackTrace?.forEach {
            if ("android.arch.lifecycle.LiveData" == it.className &&
                    "observeForever" == it.methodName) {
                return true
            }
        }
        return false
    }
}