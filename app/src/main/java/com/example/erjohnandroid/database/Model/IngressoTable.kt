package com.example.erjohnandroid.database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Ingresso")
data class IngressoTable(
    @PrimaryKey(autoGenerate = true) val Id: Int,
    val TotalCollection:Double?,
    val ManualTicket:Double?,
    val CancelledTicket:Double?,
    val TotalExpenses:Double?,
    val TotalWitholding:Double?,
    val DriverName:String?,
    val DriverCommission:Double?,
    val DriverBonus:Double?,
    val ConductorName:String?,
    val ConductorCommission:Double?,
    val ConductorBonus:Double,
    val Net:Double?,
    val PartialRemit:Double?,
    val FinalRemit:Double?,
    val ShororOver:Double?,
    var InFault:String?,
    val DateTimeStamp:String?,
    val ingressoRefId:Int?

)
