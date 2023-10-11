package com.example.erjohnandroid.printertwo.aop
import android.annotation.SuppressLint
import android.app.Application
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class SingleClick(
    /**
     * 快速点击的间隔
     */
    val value: Long = 2000
)
