package com.example.erjohnandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.Sycnh_InspectionReportTable
import com.example.erjohnandroid.database.Model.Synch_mpadAssignmentsTable

@Dao
interface Synch_mPadAssignmentDao {

    @Insert
    fun insert_synch_mpad(entity:List<Synch_mpadAssignmentsTable>)

    @Query("Select * from CopymPadAssignments")
    fun get_synch_mpad():List<Synch_mpadAssignmentsTable>

    @Query("DELETE FROM CopymPadAssignments")
    fun truncatecopympadAssignment()
}