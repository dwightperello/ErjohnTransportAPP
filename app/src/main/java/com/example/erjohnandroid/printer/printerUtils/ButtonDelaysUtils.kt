package com.example.erjohnandroid.printer.printerUtils

object ButtonDelayUtils {
    private var lastClickTime: Long = 0

    fun isFastDoubleClick(): Boolean {
        val time = System.currentTimeMillis()
        if (time - lastClickTime < 500) {
            return true
        }
        lastClickTime = time
        return false
    }
}
