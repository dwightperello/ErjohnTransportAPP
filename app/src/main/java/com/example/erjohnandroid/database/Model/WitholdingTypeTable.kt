package com.example.erjohnandroid.database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "WitholdingType")
data class WitholdingTypeTable(
    @PrimaryKey(autoGenerate = true) val WitholdingTypeId:Int,
    val id: Int?,
    val type: String?
)
