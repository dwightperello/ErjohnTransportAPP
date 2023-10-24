package com.example.erjohnandroid.database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CopyTripCost")
data class Synch_TripCostTable(
    @PrimaryKey(autoGenerate = true) val Id: Int,
    val amount: Double?,
    val costType: String?,
    val dateTimeStamp: String?,
    val driverConductorName: String?,
    val line: String?,
    val ingressoRefId:Int?
)
