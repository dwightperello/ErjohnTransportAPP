package com.example.erjohnandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.ExpensesTypeTable
import com.example.erjohnandroid.database.Model.InspectionReportTable

@Dao
interface InspectionReportDao {
    @Insert
    fun insertInspectionReportBulk(entity:InspectionReportTable)

    @Query("SELECT * FROM InspectionReport")
    fun getInspectionReport(): List<InspectionReportTable>

    @Query("DELETE FROM InspectionReport")
    fun truncatinspection()
}