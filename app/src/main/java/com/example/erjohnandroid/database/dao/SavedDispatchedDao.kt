package com.example.erjohnandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.externalDispatch.SavedDispatchInfo

@Dao
interface SavedDispatchedDao {


    @Query("SELECT * FROM SavedDispatch ")
    fun checkIfalreadyDispatch(): SavedDispatchInfo

    @Query("UPDATE SavedDispatch SET busNumber = :busnumber, conductorName = :conductorName, isDispatched =:isDispatched, dispatcherName =:dispatcherName, driverName =:driverName, line =:line, LineId =:LineId, mPadUnit =:mPadUnit, reverse =:reverse,orginalTicketnumber =:orginalTicketnumber, direction =:direction, ingressoRefId =:ingressoRefId  WHERE SavedDispatchId = 1")
    fun updateTSavedDispatch(busnumber: String, conductorName: String,isDispatched:Boolean,dispatcherName:String,driverName:String,line:String,LineId:Int, mPadUnit:String,reverse:Int,orginalTicketnumber:Int,direction:String,ingressoRefId:Int)

    @Query("UPDATE SavedDispatch SET  direction =:direction, reverse =:reverse  WHERE SavedDispatchId = 1")
    fun updateTSavedDispatchdirection(direction:String, reverse:Int)

    @Query("UPDATE SavedDispatch SET  isDispatched =:isDispatched  WHERE SavedDispatchId = 1")
    fun updateIsDispatched(isDispatched:Boolean)

    @Query("UPDATE SavedDispatch SET  reverse =:reverse  WHERE SavedDispatchId = 1")
    fun updatereverse( reverse:Int)

    @Insert
    fun insertSaveDispatch(entity: SavedDispatchInfo)

}