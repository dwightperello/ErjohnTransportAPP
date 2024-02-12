package com.example.erjohnandroid.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.TripCostTable
import com.example.erjohnandroid.database.Model.TripTicketTable
import com.example.erjohnandroid.database.Model.TripWitholdingTable
import com.example.erjohnandroid.database.Model.convertions.TripCostTotal
import com.example.erjohnandroid.database.Model.convertions.WitholdingTotal

@Dao
interface TripWitholdingDao {

    @Query("Select * from TripWitholding where ingressoRefId = :refid")
    fun gettripwitholding(refid:Int):List<TripWitholdingTable>

    @Query("UPDATE TripWitholding SET amount = :amount WHERE ingressoRefId = :refid AND witholdingType = :witholdingtype")
    fun updateTripWitholding(refid: Int, amount: Double, witholdingtype: String): Unit

    @Insert
    fun insertripwitholdingBulk(entity:TripWitholdingTable)

    @Query("Select * from TripWitholding")
    fun getAllTripWitholding(): LiveData<List<TripWitholdingTable>>

    @Query("DELETE FROM TripWitholding where TripwitholdingId =:id")
    fun deleteTripCostItem(id:Int)
    @Query("DELETE FROM TripWitholding")
    fun truncatetripWitholding()

    @Query("SELECT sum(amount) as total FROM TripWitholding")
    fun getTotalAmountWitholdingcost(): WitholdingTotal

    @Query("SELECT sum(amount) as total FROM TripWitholding")
    fun witholdingtotalamount():LiveData<Double>

    @Query("SELECT sum(amount) as total FROM TripWitholding")
    fun witholdingtotalamounttwo():Double
}