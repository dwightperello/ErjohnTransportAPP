package com.example.erjohnandroid.database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CopyTriptickets")
data class Sycn_TripticketTable(
    @PrimaryKey(autoGenerate = true) val Id: Int,
    val amount: Double?,
    val conductorName: String?,
    val dateTimeStamp: String?,
    val destination: String?,
    val driverName: String?,
    val line: String?,
    val mPadUnit: String?,
    val origin: String?,
    val passengerType: String?,
    var titcketNumber: String?,
    val qty:Int,
    val ingressoRefId:Int?,
    val reverse:Int?


    )
