package com.example.erjohnandroid.database.Model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "PassengerType")
data class PassengerTypeTable(
    @PrimaryKey(autoGenerate = true) val PassengerTypeId:Int,
    val discount: Double?,
    val id: Int?,
    val name: String?,
    val tag: Int?,


)
