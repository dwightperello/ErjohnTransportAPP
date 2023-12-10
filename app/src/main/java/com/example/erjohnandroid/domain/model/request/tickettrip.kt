package com.example.erjohnandroid.domain.model.request

data class tickettrip(
    val amount: Double,
    val conductorName: String,
    val dateTimeStamp: String,
    val destination: String,
    val driverName: String,
    val id: Int,
    val ingreId: Int,
    val line: String,
    val mPadUnit: String,
    val origin: String,
    val passengerType: String,
    val qty: Int,
    val titcketNumber: String,
    val reverse:Int
)
