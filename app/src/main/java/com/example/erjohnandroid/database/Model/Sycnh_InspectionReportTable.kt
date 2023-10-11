package com.example.erjohnandroid.database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CopyInspectionReport")
data class Sycnh_InspectionReportTable(
    @PrimaryKey(autoGenerate = true) val Id: Int,
    val actualPassengerCount: Int?,
    val dateTimeStamp: String?,
    val difference: Int?,
    val direction: String?,
    val inspectorName: String?,
    val line: String?,
    val lineSegment: String?,
    val mPadUnit: String?,
    val qty: Int?
)
