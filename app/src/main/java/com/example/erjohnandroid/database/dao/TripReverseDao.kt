package com.example.erjohnandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.TerminalTable
import com.example.erjohnandroid.database.Model.TripReverseTable

@Dao
interface TripReverseDao {

    @Insert
    fun insertTripReverseBulk(entity:List<TripReverseTable>)

    @Query("SELECT * FROM TripReverse")
    fun getTripreverse(): List<TripReverseTable>

    @Query("DELETE FROM TripReverse")
    fun truncateTripReverse()
}