package com.example.erjohnandroid.util

import android.app.Activity
import android.content.Intent
import androidx.annotation.AnimRes


inline fun <reified T : Activity> Activity.startActivityWithAnimation(
    @AnimRes enterAnim: Int,
    @AnimRes exitAnim: Int
) {
    val intent = Intent(this, T::class.java)
    startActivity(intent)
    overridePendingTransition(enterAnim, exitAnim)
}