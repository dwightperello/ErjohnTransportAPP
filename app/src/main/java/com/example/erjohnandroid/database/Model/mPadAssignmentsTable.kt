package com.example.erjohnandroid.database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mPadAssignments")
data class mPadAssignmentsTable(
    @PrimaryKey(autoGenerate = true) val mPadAssignmentId:Int,
    val busNumber: String?,
    val conductorName: String?,
    val dataTimeStamp: String?,
    val dispatcherName: String?,
    val driverName: String?,
    val line: String?,
    val mPadUnit: String?,
    val ingressoRefId:Int?

)
