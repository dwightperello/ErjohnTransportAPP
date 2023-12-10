package com.example.erjohnandroid.database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CopyTripReverse")
data class Synch_TripReverseTable(
    @PrimaryKey(autoGenerate = true) val Id: Int,
    val amount: Double,
    val dateTimeStamp: String,
    val deviceName: String,
    val direction: String,
    val reverseId: Int,
    val terminal: String,
    val ingressoRefId:Int
)
