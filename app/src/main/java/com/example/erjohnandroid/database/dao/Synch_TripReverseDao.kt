package com.example.erjohnandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.Synch_TripReverseTable
import com.example.erjohnandroid.database.Model.Synch_partialremitTable

@Dao
interface Synch_TripReverseDao {

    @Insert
    fun insert_synch_TripReverse(entity:List<Synch_TripReverseTable>)

    @Query("Select * from CopyTripReverse where ingressoRefId = :refid")
    fun get_synch_TripReverse(refid:Int):List<Synch_TripReverseTable>

    @Query("DELETE FROM CopyTripReverse")
    fun truncatecopyTripReverse()
}