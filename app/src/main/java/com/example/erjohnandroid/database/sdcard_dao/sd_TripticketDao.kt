package com.example.erjohnandroid.database.sdcard_dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.TripTicketTable

@Dao
interface sd_TripticketDao {

    @Query("Select * from TripTickets")
    fun getTriptikcet():List<TripTicketTable>

    @Insert
    fun inserTripticketBulk(entity:TripTicketTable)

    @Query("SELECT * FROM TripTickets where dateTimeStamp = :datetimestamp")
   suspend fun selectTicket_bydate(datetimestamp:String):List<TripTicketTable>
}