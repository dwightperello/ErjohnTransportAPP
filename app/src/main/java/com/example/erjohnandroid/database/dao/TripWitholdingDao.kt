package com.example.erjohnandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.TripTicketTable
import com.example.erjohnandroid.database.Model.TripWitholdingTable
import com.example.erjohnandroid.database.Model.convertions.TripCostTotal
import com.example.erjohnandroid.database.Model.convertions.WitholdingTotal

@Dao
interface TripWitholdingDao {

    @Query("Select * from TripWitholding")
    fun gettripwitholding():List<TripWitholdingTable>

    @Insert
    fun insertripwitholdingBulk(entity:List<TripWitholdingTable>)

    @Query("DELETE FROM TripWitholding")
    fun truncatetripWitholding()

    @Query("SELECT sum(amount) as total FROM TripWitholding")
    fun getTotalAmountWitholdingcost(): WitholdingTotal
}