package com.example.erjohnandroid.database.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TripTickets")
data class TripTicketTable(
    @PrimaryKey(autoGenerate = true) val TripTicketId:Int,
    val amount: Double?,
    val conductorName: String?,
    val dateTimeStamp: String?,
    val destination: String?,
    val driverName: String?,
    val line: String?,
    val mPadUnit: String?,
    val origin: String?,
    val passengerType: String?,
    var titcketNumber: String?,
    val KMOrigin: Int?,
    val KmDestination: Int,
    val qty:Int,
    //NOT INCLUDED IN THE API
    val tripReverse:Int?,


)
