package com.example.erjohnandroid.domain.model.request

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TripCost(

    val amount: Double,
    val costType: String,
    val dateTimeStamp: String,
    val driverConductorName: String,
    val line: String

):Parcelable