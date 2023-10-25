package com.example.erjohnandroid.database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "HotSpots")
data class HotSpotsTable(
    @PrimaryKey(autoGenerate = true) val HotSpotsId: Int = 0,
    val fare: Double,
    val id: Int,
    val lineid: Int,
    val modeid: Int?,
    val namE2: String?,
    val name: String?,
    val pointfrom: Int,
    val pointto: Int,
    val tag: String?
)
