package com.example.erjohnandroid.database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TripWitholding")
data class TripWitholdingTable(
    @PrimaryKey(autoGenerate = true) val TripwitholdingId:Int,
    val amount: Double?,
    val dateTimeStamp: String?,
    val mPadUnit: String?,
    val name: String?,
    val witholdingType: String?
)
