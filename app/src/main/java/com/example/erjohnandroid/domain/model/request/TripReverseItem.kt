package com.example.erjohnandroid.domain.model.request

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class TripReverseItem(
    val amount: Double,
    val dateTimeStamp: String,
    val deviceName: String,
    val direction: String,
    val reverseId: Int,
    val terminal: String

):Parcelable