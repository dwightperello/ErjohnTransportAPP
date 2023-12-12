package com.example.erjohnandroid.database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Fares")
data class FareTable(
    @PrimaryKey(autoGenerate = true) val FareId: Int,
    val baseAmount: Double,
    val discountAmount: Double,
    val exceedAmount: Double,
    val id: Int,
    val name: String,
    val specialExceedAmount: Double
)
