package com.example.erjohnandroid.database.Model.externalDispatch

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SavedDispatch")
data class SavedDispatchInfo(
    @PrimaryKey(autoGenerate = true) val SavedDispatchId:Int=0,
    val busNumber: String,
    val conductorName: String,
    val isDispatched:Boolean,
    val dispatcherName: String,
    val driverName: String,
    val line: String,
    val LineId:Int,
    val mPadUnit: String,
    val ingressoRefId:Int,
    val reverse: Int,
    val orginalTicketnumber:Int,
    val machineName:String,
    val permitNumber:String,
    val serialNumber:String,
    val direction:String
)
