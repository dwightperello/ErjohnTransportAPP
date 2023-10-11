package com.example.erjohnandroid.printertwo
import android.annotation.SuppressLint
import android.app.Application

object Utils {

    private const val TAG = "Utils"

    private var sApplication: Application? = null
    private var sUtils: Utils? = null

    fun getInstance(): Utils {
        if (sUtils == null) {
            sUtils = Utils
        }
        return sUtils!!
    }

    fun init(app: Application?): Utils {
        getInstance()
        if (sApplication == null) {
            if (app == null) {
                sApplication = getApplicationByReflect()
            } else {
                sApplication = app
            }
        } else {
            if (app != null && app.javaClass != sApplication?.javaClass) {
                sApplication = app
            }
        }
        return sUtils!!
    }

    fun getApp(): Application {
        if (sApplication != null) {
            return sApplication!!
        }
        val app = getApplicationByReflect()
        init(app)
        return app
    }

    private fun getApplicationByReflect(): Application {
        try {
            @SuppressLint("PrivateApi")
            val activityThread = Class.forName("android.app.ActivityThread")
            val thread = activityThread.getMethod("currentActivityThread").invoke(null)
            val app = activityThread.getMethod("getApplication").invoke(thread)
            if (app == null) {
                throw NullPointerException("u should init first")
            }
            return app as Application
        } catch (e: Exception) {
            e.printStackTrace()
        }
        throw NullPointerException("u should init first")
    }
}
