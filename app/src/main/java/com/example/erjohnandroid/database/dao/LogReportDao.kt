package com.example.erjohnandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.LogReport
import com.example.erjohnandroid.database.Model.TripReverseTable

@Dao
interface LogReportDao {

    @Insert
    fun insertLogReportBulk(entity:List<LogReport>)

    @Insert
    fun insertLogReport(entity:LogReport)

    @Query("SELECT * FROM LogReport")
    fun getLogReport(): List<LogReport>

    @Query("DELETE FROM LogReport")
    fun truncateLogReport()


}