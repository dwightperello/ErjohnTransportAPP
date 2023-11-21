package com.example.erjohnandroid.database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mPadUnits")
data class mPadUnitsTable(
    @PrimaryKey(autoGenerate = true) val mpadUnitsId: Int = 0,
    val id: Int,
    val machineName: String?,
    val name: String?,
    val permit: String?,
    val permitNumber: String?,
    val serialNumber: String?,
    val tag: Int?
)
