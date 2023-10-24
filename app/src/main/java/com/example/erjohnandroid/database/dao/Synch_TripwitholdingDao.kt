package com.example.erjohnandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.Sycn_TripticketTable
import com.example.erjohnandroid.database.Model.Synch_TripwitholdingTable

@Dao
interface Synch_TripwitholdingDao {

    @Insert
    fun insert_synch_witholding(entity:List<Synch_TripwitholdingTable>)

    @Query("Select * from CopyTripWitholding where ingressoRefId = :refid")
    fun get_synch_witholding(refid:Int):List<Synch_TripwitholdingTable>

    @Query("DELETE FROM CopyTripWitholding")
    fun truncatecopywitholdings()
}