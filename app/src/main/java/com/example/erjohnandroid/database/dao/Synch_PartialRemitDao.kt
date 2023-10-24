package com.example.erjohnandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.Synch_mpadAssignmentsTable
import com.example.erjohnandroid.database.Model.Synch_partialremitTable

@Dao
interface Synch_PartialRemitDao {

    @Insert
    fun insert_synch_partial_remit(entity:List<Synch_partialremitTable>)

    @Query("Select * from CopyPartialRemit where ingressoRefId = :refid")
    fun get_synch_partial_remit(refid:Int):List<Synch_partialremitTable>

    @Query("DELETE FROM CopyPartialRemit")
    fun truncatecopypartilaremit()
}