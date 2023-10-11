package com.example.erjohnandroid.database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CopymPadAssignments")
data class Synch_mpadAssignmentsTable(
    @PrimaryKey(autoGenerate = true) val Id: Int,
    val busNumber: String?,
    val conductorName: String?,
    val dataTimeStamp: String?,
    val dispatcherName: String?,
    val driverName: String?,
    val line: String?,
    val mPadUnit: String?
)
