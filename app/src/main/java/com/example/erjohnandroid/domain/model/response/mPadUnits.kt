package com.example.erjohnandroid.domain.model.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class mPadUnitsItem(
    val id: Int,
    val machineName: String?,
    val name: String?,
    val permit: String?,
    val permitNumber: String?,
    val serialNumber: String?,
    val tag: Int?
):Parcelable