package com.example.erjohnandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.TripCostTable
import com.example.erjohnandroid.database.Model.TripTicketTable
import com.example.erjohnandroid.database.Model.convertions.TicketTotal
import com.example.erjohnandroid.database.Model.convertions.TripAmountPerReverse
import com.example.erjohnandroid.database.Model.convertions.TripTicketGroupCount


@Dao
interface TripTicketDao {

    @Query("Select * from TripTickets")
    fun getTriptikcet():List<TripTicketTable>

    @Insert
    fun inserTripticketBulk(entity:TripTicketTable)

    @Insert
    fun inserTripticketBulktwo(entity:List<TripTicketTable>)

    @Query("select * from TripTickets where KmDestination <= :kmorigin AND tripReverse = :reverse and passengerType != 'Baggage'" )
    fun getRemNort(kmorigin:Int,reverse:Int):List<TripTicketTable>

    @Query("Select * from TripTickets where KmDestination >= :kmorigin AND tripReverse = :reverse and passengerType != 'Baggage'")
    fun getRemSouth(kmorigin:Int,reverse:Int):List<TripTicketTable>

    @Query("SELECT tripReverse, COUNT(*) AS group_count, sum(amount) as sumamount FROM TripTickets GROUP BY tripReverse")
    fun getReverse(): List<TripTicketGroupCount>

    @Query("SELECT sum(amount) as total FROM TripTickets")
    fun getTotalAmount(): TicketTotal

    @Query("select * from TripTickets where tripReverse = :reverse ")
    fun getticketdetails(reverse:Int):List<TripTicketTable>

    @Query("DELETE FROM TripTickets")
    fun truncateTripticket()

    @Query("SELECT  COUNT(amount) AS ticket_count, sum(amount) as sumamount FROM TripTickets where tripReverse = :reverse")
    fun getPerTripAmount(reverse:Int): TripAmountPerReverse

}