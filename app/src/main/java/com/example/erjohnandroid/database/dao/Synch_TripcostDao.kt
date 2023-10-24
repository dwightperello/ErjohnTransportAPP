package com.example.erjohnandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.Synch_TripCostTable
import com.example.erjohnandroid.database.Model.Synch_partialremitTable

@Dao
interface Synch_TripcostDao {

    @Insert
    fun insert_synch_trip_cost(entity:List<Synch_TripCostTable>)

    @Query("Select * from CopyTripCost where ingressoRefId = :refid")
    fun get_synch_trip_cost(refid:Int):List<Synch_TripCostTable>

    @Query("DELETE FROM CopyTripCost")
    fun truncatecopytripcost()
}