package com.example.erjohnandroid.database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TicketCounter")
data class TicketCounterTable(
    @PrimaryKey(autoGenerate = true) val Id: Int,
    val ticketnumber:Int,
    val ingressoRefId:Int
)
