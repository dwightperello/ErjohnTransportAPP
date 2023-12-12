package com.example.erjohnandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.LogReport
import com.example.erjohnandroid.database.Model.Synch_LogReport
import com.example.erjohnandroid.database.Model.Synch_TripCostTable
import com.example.erjohnandroid.database.Model.Synch_TripReverseTable

@Dao
interface Synch_LogReportDao {

    @Insert
    fun insert_synch_LogReport(entity:List<Synch_LogReport>)

    @Query("Select * from CopyLogReport where ingressoRefId = :refid")
    fun get_synch_LogReport(refid:Int):List<Synch_LogReport>

    @Query("DELETE FROM CopyLogReport")
    fun truncatecopyLogReport()
}