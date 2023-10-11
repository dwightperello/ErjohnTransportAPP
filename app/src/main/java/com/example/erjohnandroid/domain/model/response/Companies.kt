package com.example.erjohnandroid.domain.model.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize



@Parcelize
data class CompaniesItem(
    val busType: List<BusType>,
    val companyName: String,
    val id: Int,
    val remarks: String,
    val tag: Int
):Parcelable

@Parcelize
data class BusType(
    val busInfo: List<BusInfos>,
    val companyId: Int,
    val id: Int,
    val type: String
):Parcelable

@Parcelize
data class BusInfos(
    val busNumber: Int,
    val busTypeId: Int,
    val id: Int,
    val plateNumber: String
):Parcelable