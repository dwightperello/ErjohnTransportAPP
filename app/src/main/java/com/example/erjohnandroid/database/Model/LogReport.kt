package com.example.erjohnandroid.database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LogReport")
data class LogReport(
    @PrimaryKey(autoGenerate = true) val LogReportId:Int,
    val dateTimeStamp: String,
    val description: String,
    val deviceName: String,
    val ingressoRefId:Int?,
)