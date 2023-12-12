package com.example.erjohnandroid.database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CopyLogReport")
data class Synch_LogReport(
    @PrimaryKey(autoGenerate = true) val Id: Int,
    val dateTimeStamp: String,
    val description: String,
    val deviceName: String,
    val ingressoRefId:Int?,
)
