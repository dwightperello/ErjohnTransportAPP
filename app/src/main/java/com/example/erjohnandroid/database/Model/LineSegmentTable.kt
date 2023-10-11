package com.example.erjohnandroid.database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LineSegment")
data class LineSegmentTable(
    @PrimaryKey(autoGenerate = true) val LineSegmentid: Int,
    val id: Int?,
    val kmPoint: Int?,
    val lineId: Int?,
    val name: String?,
    val remarks: String?,
    val tag: Int?
)
