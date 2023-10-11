package com.example.erjohnandroid.domain.model.request

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InspectionReports(

    val actualPassengerCount: Int,
    val dateTimeStamp: String,
    val difference: Int,
    val direction: String,
    val inspectorName: String,
    val line: String,
    val lineSegment: String,
    val mPadUnit: String,
    val qty: Int

):Parcelable
