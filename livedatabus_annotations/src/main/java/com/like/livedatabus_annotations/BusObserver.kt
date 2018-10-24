package com.like.livedatabus_annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class BusObserver(
        val value: Array<String>,
        val tag2: String = "",
        val isSticky: Boolean = false
)
