package com.example.erjohnandroid.domain.model.request

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Ingresso(
    val cancelledTicket: Double,
    val conductorCommission: Double,
    val conductorName: String,
    val dateTimeStamp: String,
    val driverCommission: Double,
    val driverName: String,
    val finalRemit: Double,
    val inFault: String,
    val manualTicket: Double,
    val net: Double,
    val partialRemit: Double,
    val shororOver: Double,
    val totalCollection: Double,
    val totalExpenses: Double,
    val totalWitholding: Double
):Parcelable