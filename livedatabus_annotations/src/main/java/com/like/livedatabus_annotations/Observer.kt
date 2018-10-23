package com.like.livedatabus_annotations

import com.sun.xml.internal.fastinfoset.util.StringArray
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
annotation class Observer {
    /**
     * 标签数组
     */
    var value: StringArray,
    /**
     * 事件的参数，类似于请求码requestCode
     */
    var code: String = "",
    /**
     * 是否粘性事件
     */
    val isSticky: Boolean = false
}
