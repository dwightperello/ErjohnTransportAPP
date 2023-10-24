package com.example.erjohnandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.LinesTable
import com.example.erjohnandroid.database.Model.mPadAssignmentsTable

@Dao
interface mPadAssignmentsDao {

    @Query("Select * from mPadAssignments where ingressoRefId = :refid")
    fun getmpadAssignment(refid:Int):List<mPadAssignmentsTable>

    @Insert
    fun insertmpadassignmentBulk(entity:List<mPadAssignmentsTable>)

    @Query("DELETE FROM mPadAssignments")
    fun truncatempad()
}