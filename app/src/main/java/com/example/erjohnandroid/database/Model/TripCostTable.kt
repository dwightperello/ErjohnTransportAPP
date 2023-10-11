package com.example.erjohnandroid.database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TripCost")
data class TripCostTable(
    @PrimaryKey(autoGenerate = true) val TripCostId:Int,
    val amount: Double?,
    val costType: String?,
    val dateTimeStamp: String?,
    val driverConductorName: String?,
    val line: String?
)
