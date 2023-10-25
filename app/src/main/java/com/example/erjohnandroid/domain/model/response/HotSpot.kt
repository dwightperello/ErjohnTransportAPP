package com.example.erjohnandroid.domain.model.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class HotSpotItem(
    val fare: Double,
    val id: Int,
    val lineid: Int,
    val modeid: Int?,
    val namE2: String?,
    val name: String?,
    val pointfrom: Int,
    val pointto: Int,
    val tag: String?
):Parcelable