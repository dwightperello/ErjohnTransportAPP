package com.example.erjohnandroid.domain.model.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class PassengerTypeItem(
    val discount: Double,
    val id: Int,
    val name: String,
    val tag: Int
):Parcelable