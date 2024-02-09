package com.example.erjohnandroid.util

import android.content.Context
import android.widget.Toast
import com.example.erjohnandroid.R
import io.github.muddz.styleabletoast.StyleableToast

fun showCustomToast(context: Context, message: String) {
    StyleableToast.makeText(context, message, Toast.LENGTH_SHORT, R.style.mytoast).show()
}