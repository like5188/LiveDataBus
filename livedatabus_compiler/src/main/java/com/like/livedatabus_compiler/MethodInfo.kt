package com.like.livedatabus_compiler

import javax.lang.model.type.TypeMirror

class MethodInfo {
    var methodName = ""// 被@BusObserver注解标注的方法名字
    var tag1: Array<String>? = null
    var tag2 = ""
    var isSticky = false
    var paramType: TypeMirror? = null// 被@BusObserver注解标注的方法的参数类型。只支持一个参数
}