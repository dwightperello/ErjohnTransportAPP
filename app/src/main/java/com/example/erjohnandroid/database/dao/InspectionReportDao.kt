package com.example.erjohnandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.ExpensesTypeTable
import com.example.erjohnandroid.database.Model.InspectionReportTable
import com.example.erjohnandroid.database.Model.TripTicketTable

@Dao
interface InspectionReportDao {
    @Insert
    fun insertInspectionReportBulk(entity:InspectionReportTable)

    @Query("SELECT * FROM InspectionReport where ingressoRefId = :refid")
    fun getInspectionReport(refid:Int): List<InspectionReportTable>

    @Query("DELETE FROM InspectionReport")
    fun truncatinspection()

    @Query ("SELECT * FROM TripTickets WHERE KmDestination >= :searchkm and tripReverse = :tripreverse and passengerType != 'Baggage'")
    fun getTripticketsAfterInspection(searchkm: Int,tripreverse:Int ): List<TripTicketTable>

    @Query ("SELECT * FROM TripTickets WHERE KmDestination <= :searchkm and tripReverse = :tripreverse and passengerType != 'Baggage'")
    fun getTripticketsAfterInspectionNorth(searchkm: Int,tripreverse:Int): List<TripTicketTable>
}