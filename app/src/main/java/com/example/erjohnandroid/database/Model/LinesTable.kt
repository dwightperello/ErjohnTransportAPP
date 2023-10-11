package com.example.erjohnandroid.database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.erjohnandroid.domain.model.response.LineSegment

@Entity(tableName = "Lines")
data class LinesTable(
    @PrimaryKey(autoGenerate = true) val Lineid: Int = 0,
    val id: Int?,
    val name: String?,
    val remarks: String?,
    val tag: Int?
)
