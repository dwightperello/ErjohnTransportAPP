package com.example.erjohnandroid.database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FareByKm(

    @PrimaryKey(autoGenerate = true) val farekmId: Int?,
    val amount:Int?,
    val upperkmlimit:Int?,
    val lineid: Int?,
    val lowerkmlimit:Int?,
    val totalkm:Int?,
    val id:Int?,
    val discountrate:Int?,




)
