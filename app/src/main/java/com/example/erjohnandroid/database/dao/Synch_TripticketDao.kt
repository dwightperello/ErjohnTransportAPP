package com.example.erjohnandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.LinesTable
import com.example.erjohnandroid.database.Model.Sycn_TripticketTable

@Dao
interface Synch_TripticketDao {

    @Insert
    fun insertTripTicket(entity:List<Sycn_TripticketTable>)

    @Query("Select * from CopyTriptickets")
    fun getAllsynctickets():List<Sycn_TripticketTable>

    @Query("DELETE FROM CopyTriptickets")
    fun truncatecopytripticket()
}