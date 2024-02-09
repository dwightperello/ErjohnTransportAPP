package com.example.erjohnandroid.util

import android.content.Context
import android.widget.Switch
import android.widget.Toast
import com.example.erjohnandroid.R
import com.google.android.gms.common.internal.GmsLogger
import io.github.muddz.styleabletoast.StyleableToast

val computePitxFare: (Int, Int) -> Double = { destination,origin ->
    when (GlobalVariable.direction){
        "South" ->{
            val kmDifference = destination - origin

        }
    }

    val fare=0.0
    fare
}