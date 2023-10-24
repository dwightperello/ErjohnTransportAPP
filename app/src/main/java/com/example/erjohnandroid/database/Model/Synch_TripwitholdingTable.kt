package com.example.erjohnandroid.database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CopyTripWitholding")
data class Synch_TripwitholdingTable(
    @PrimaryKey(autoGenerate = true) val Id: Int,
    val amount: Double?,
    val dateTimeStamp: String,
    val mPadUnit: String?,
    var name: String?,
    val witholdingType: String?,
    val ingressoRefId:Int?
)
