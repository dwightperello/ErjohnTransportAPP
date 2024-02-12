package com.example.erjohnandroid.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.PassengerTypeTable
import com.example.erjohnandroid.database.Model.TripCostTable
import com.example.erjohnandroid.database.Model.convertions.TicketTotal
import com.example.erjohnandroid.database.Model.convertions.TripCostTotal

@Dao
interface TripCostDao {

    @Query("Select * from TripCost where ingressoRefId = :refid")
    fun getTripCost(refid:Int):List<TripCostTable>

    @Insert
    fun inserTripcostBulk(entity:TripCostTable)

    @Query("Select * from TripCost")
    fun getAllTripCost():LiveData<List<TripCostTable>>

    @Query("DELETE FROM TripCost")
    fun truncateTripcost()

    @Query("DELETE FROM TripCost where TripCostId =:id")
    fun deleteTripCostItem(id:Int)

    @Query("SELECT sum(amount) as total FROM TripCost")
    fun getTotalAmountTripcost(): TripCostTotal

    @Query("SELECT sum(amount) as total FROM TripCost")
    fun getTotalTripCost():LiveData<Double>

    @Query("SELECT sum(amount) as total FROM TripCost")
    fun getTotalTripCosttwo():Double

    @Query("UPDATE TripCost SET amount = :amount WHERE ingressoRefId = :refid AND costType = :costType")
    fun updateExpenses(refid: Int, amount: Double, costType: String): Unit

}