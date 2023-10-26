package com.example.erjohnandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.InspectionReportTable
import com.example.erjohnandroid.database.Model.LinesTable
import com.example.erjohnandroid.database.Model.TicketCounterTable

@Dao
interface TicketNumDAO {

    @Query("SELECT * FROM TicketCounter ")
    fun getTicketstart(): TicketCounterTable

    @Query("UPDATE TicketCounter SET ticketnumber = :ticknum, ingressoRefId = :ingressrefid WHERE Id = :id")
    fun updateTicketstart(ticknum: Int, ingressrefid: Int, id: Int)

    @Insert
    fun insertticketnum(entity:TicketCounterTable)

}