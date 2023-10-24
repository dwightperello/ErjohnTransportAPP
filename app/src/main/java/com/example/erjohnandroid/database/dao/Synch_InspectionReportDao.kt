package com.example.erjohnandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.Sycn_TripticketTable
import com.example.erjohnandroid.database.Model.Sycnh_InspectionReportTable

@Dao
interface Synch_InspectionReportDao {

    @Insert
    fun insertsynch_inspection(entity:List<Sycnh_InspectionReportTable>)

    @Query("Select * from CopyInspectionReport where ingressoRefId = :refid")
    fun getsynch_inspection(refid:Int):List<Sycnh_InspectionReportTable>

    @Query("DELETE FROM CopyInspectionReport")
    fun truncatecopyinspection()
}