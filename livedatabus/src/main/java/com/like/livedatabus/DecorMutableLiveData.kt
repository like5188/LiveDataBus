package com.like.livedatabus

import android.arch.core.internal.SafeIterableMap
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import java.util.Map

/**
 * 解决问题：在使用这个LiveDataBus的过程中，订阅者会收到订阅之前发布的消息。
 *
 * 原因：对于LiveData，其初始的version是-1，当我们调用了其setValue或者postValue，其vesion会+1；
 * 对于每一个观察者的封装ObserverWrapper，其初始version也为-1，也就是说，每一个新注册的观察者，其version为-1；
 * 当LiveData设置这个ObserverWrapper的时候，如果LiveData的version大于ObserverWrapper的version，
 * LiveData就会强制把当前value推送给Observer。
 *
 * 解决办法：只需要在注册一个新的订阅者的时候把ObserverWrapper的version设置成跟LiveData的version一致即可。
 * ①、采用observe注册时：
 * 会创建一个LifecycleBoundObserver，LifecycleBoundObserver是ObserverWrapper的派生类。
 * 然后会把这个LifecycleBoundObserver放入一个私有Map容器mObservers中。
 * 无论ObserverWrapper还是LifecycleBoundObserver都是私有的或者包可见的，
 * 所以无法通过继承的方式更改LifecycleBoundObserver的version。
 * 那么能不能从Map容器mObservers中取到LifecycleBoundObserver，然后再更改version呢？
 * 答案是肯定的，通过查看SafeIterableMap的源码我们发现有一个protected的get方法。
 * 因此，在调用observe的时候，我们可以通过反射拿到LifecycleBoundObserver，
 * 再把LifecycleBoundObserver的version设置成和LiveData一致即可。
 * ②、采用observeForever注册时：
 * 生成的wrapper不是LifecycleBoundObserver，而是AlwaysActiveObserver，
 * 而且我们也没有机会在observeForever调用完成之后再去更改AlwaysActiveObserver的version，
 * 因为在observeForever方法体内，最后调用了wrapper.activeStateChanged(true)。
 * 既然是在调用内回调的，那么我们可以写一个ObserverWrapper，把真正的回调给包装起来。
 * 把ObserverWrapper传给observeForever，那么在回调的时候我们去检查调用栈，
 * 如果回调是因为调用observeForever方法引起的，那么就不回调真正的订阅者。
 */
class DecorMutableLiveData<T> : MutableLiveData<T>() {
    /**
     * 通过 observeForever 方法注册的 Observer
     */
    private val foreverObserverMap = mutableMapOf<Observer<T>, Observer<T>>()

    override fun observeForever(observer: Observer<T>) {
        if (!foreverObserverMap.containsKey(observer)) {
            foreverObserverMap[observer] = DecorObserver(observer)
        }
        super.observeForever(foreverObserverMap[observer]!!)
    }

    override fun removeObserver(observer: Observer<T>) {
        val realObserver =
                if (foreverObserverMap.containsKey(observer)) {
                    foreverObserverMap.remove(observer)
                } else {
                    observer
                }
        super.removeObserver(realObserver!!)
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<T>) {
        super.observe(owner, observer)
        try {
            setObserverWrapperVersion(observer, getLiveDataVersion())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 获取 LiveData 的 mVersion 属性
     */
    @Throws(Exception::class)
    private fun getLiveDataVersion(): Int {
        val mVersionField = LiveData::class.java.getDeclaredField("mVersion")
        mVersionField.isAccessible = true
        return mVersionField.get(this) as Int
    }

    /**
     * 设置 ObserverWrapper 的 mLastVersion 值为 liveDataVersion
     */
    @Throws(Exception::class)
    private fun setObserverWrapperVersion(observer: Observer<T>, liveDataVersion: Int) {
        val mObserversField = LiveData::class.java.getDeclaredField("mObservers")
        mObserversField.isAccessible = true
        val getMethod = SafeIterableMap::class.java.getDeclaredMethod("get", Any::class.java)
        getMethod.isAccessible = true
        val lifecycleBoundObserverEntry = getMethod.invoke(mObserversField.get(this), observer)
        val lifecycleBoundObserver = (if (lifecycleBoundObserverEntry is Map.Entry<*, *>) {
            lifecycleBoundObserverEntry.value
        } else {
            throw NullPointerException("ObserverWrapper can not be bull!")
        })
        val mLastVersionField = lifecycleBoundObserver.javaClass.superclass!!.getDeclaredField("mLastVersion")
        mLastVersionField.isAccessible = true
        mLastVersionField.set(lifecycleBoundObserver, liveDataVersion)
    }

    private class DecorObserver<T>(private val observer: Observer<T>) : Observer<T> {

        override fun onChanged(t: T?) {
            // 如果回调是因为调用observeForever方法引起的，那么就不回调真正的订阅者。
            // 即屏蔽一注册就收到订阅之前发布的消息
            val isCallOnObserveForever =
                    Thread.currentThread().stackTrace.any {
                        LiveData::class.java.name == it.className && "observeForever" == it.methodName
                    }
            if (isCallOnObserveForever) {
                return
            }
            observer.onChanged(t)
        }

    }
}