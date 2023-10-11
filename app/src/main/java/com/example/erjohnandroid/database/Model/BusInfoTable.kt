package com.example.erjohnandroid.database.Model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.bumptech.glide.annotation.Excludes


@Entity(tableName = "BusInfo")
data class BusInfoTableItem(
    @PrimaryKey(autoGenerate = true) val BusInfoId: Int,
    val busNumber: Int?,
    val busTypeId: Int?,
    val id: Int?,
    val plateNumber: String?,
    val companyId:Int?

)