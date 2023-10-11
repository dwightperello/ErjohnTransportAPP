package com.example.erjohnandroid.domain.model.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BusInfo(
    val busNumber: Int,
    val busTypeId: Int,
    val id: Int,
    val plateNumber: String
):Parcelable
