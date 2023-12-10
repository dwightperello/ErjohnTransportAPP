package com.example.erjohnandroid.domain.model.request

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class mPadAssignments(

    val busNumber: String,
    val conductorName: String,
    val dataTimeStamp: String,
    val dispatcherName: String,
    val driverName: String,
    val line: String,
    val mPadUnit: String,
    val terminal:String

):Parcelable