package com.example.erjohnandroid.domain.model.request

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TripWitholdings(

    val amount: Double,
    val dateTimeStamp: String,
    val mPadUnit: String,
    val name: String,
    val witholdingType: String

):Parcelable