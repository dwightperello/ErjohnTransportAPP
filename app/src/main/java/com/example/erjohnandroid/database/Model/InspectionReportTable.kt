package com.example.erjohnandroid.database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "InspectionReport")
data class InspectionReportTable(
    @PrimaryKey(autoGenerate = true) val InspectionReportId:Int,
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
