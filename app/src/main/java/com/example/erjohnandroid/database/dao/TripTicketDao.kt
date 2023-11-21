package com.example.erjohnandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.TripCostTable
import com.example.erjohnandroid.database.Model.TripTicketTable
import com.example.erjohnandroid.database.Model.convertions.TicketTotal
import com.example.erjohnandroid.database.Model.convertions.TripAmountPerReverse
import com.example.erjohnandroid.database.Model.convertions.TripGross
import com.example.erjohnandroid.database.Model.convertions.TripTicketGroupCount
import com.example.erjohnandroid.database.Model.externalDispatch.TotalAmountAndTicketNumbersPerReverse


@Dao
interface TripTicketDao {

    @Query("Select * from TripTickets where ingressoRefId = :refid")
    fun getTriptikcet(refid:Int):List<TripTicketTable>

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

    //@Query("SELECT * FROM TripTickets WHERE TripTicketId IN (SELECT MIN(TripTicketId) FROM TripTickets GROUP BY tripReverse UNION SELECT MAX(TripTicketId) FROM TripTickets GROUP BY tripReverse)")
   // @Query("SELECT *, (SELECT SUM(amount) FROM TripTickets t WHERE t.tripReverse = TripTickets.tripReverse) AS totalAmount, (SELECT SUM(qty) FROM TripTickets t WHERE t.tripReverse = TripTickets.tripReverse) AS totalQty FROM TripTickets WHERE TripTicketId IN (SELECT MIN(TripTicketId) FROM TripTickets GROUP BY tripReverse UNION SELECT MAX(TripTicketId) FROM TripTickets GROUP BY tripReverse)")

   // @Query("SELECT tripReverse, SUM(amount) AS totalAmount, GROUP_CONCAT(titcketNumber) AS ticketNumbers FROM TripTickets WHERE TripTicketId IN (SELECT MIN(TripTicketId) FROM TripTickets GROUP BY tripReverse UNION SELECT MAX(TripTicketId) FROM TripTickets GROUP BY tripReverse) GROUP BY tripReverse")
  // @Query("SELECT tripReverse, SUM(amount) AS totalAmount, GROUP_CONCAT(titcketNumber) AS ticketNumbers FROM TripTickets GROUP BY tripReverse")

    @Query("SELECT tripReverse, SUM(amount) AS totalAmount, COUNT(*) AS ticketCount, " +
            "(SELECT titcketNumber FROM TripTickets WHERE TripTickets.tripReverse = t1.tripReverse ORDER BY TripTicketId ASC LIMIT 1) AS firstTicketNumber, " +
            "(SELECT titcketNumber FROM TripTickets WHERE TripTickets.tripReverse = t1.tripReverse ORDER BY TripTicketId DESC LIMIT 1) AS lastTicketNumber " +
            "FROM TripTickets t1 GROUP BY t1.tripReverse")


    fun getAllTicketsForReverse(): List<TotalAmountAndTicketNumbersPerReverse>

    //fun getAllTicketsForReverse():List<TripTicketTable>

    @Query("SELECT  COUNT(amount) AS ticket_count, sum(amount) as sumamount FROM TripTickets where tripReverse = :reverse")
    fun getPerTripAmount(reverse:Int): TripAmountPerReverse

    @Query("SELECT sum(amount) as sumamount FROM TripTickets")
    fun getGross(): TripGross

}