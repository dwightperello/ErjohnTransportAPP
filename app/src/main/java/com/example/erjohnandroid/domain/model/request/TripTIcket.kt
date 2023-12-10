package com.example.erjohnandroid.domain.model.request

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TripTIcket(

    val amount: Double,
    val conductorName: String,
    val dateTimeStamp: String,
    val destination: String,
    val driverName: String,
    val line: String,
    val mPadUnit: String,
    val origin: String,
    val passengerType: String,
    val qty: Int,
    val titcketNumber: String,
    val reverse:Int

):Parcelable