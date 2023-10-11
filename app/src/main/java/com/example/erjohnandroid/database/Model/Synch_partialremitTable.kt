package com.example.erjohnandroid.database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CopyPartialRemit")
data class Synch_partialremitTable(
    @PrimaryKey(autoGenerate = true) val Id: Int,
    val CashierName: String?,
    val Amount: Double?,
    val AmountRemited: Double?,
    val Line: String?,
    val DateTimeStamp :String?
)
