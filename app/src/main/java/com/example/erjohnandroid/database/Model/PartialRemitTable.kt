package com.example.erjohnandroid.database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PartialRemit")
data class PartialRemitTable (
    @PrimaryKey(autoGenerate = true) val PartialremitId: Int,
    val CashierName: String?,
    val Amount: Double?,
    val AmountRemited: Double?,
    val Line: String?,
    val DateTimeStamp :String,
    val ingressoRefId:Int?
        )
