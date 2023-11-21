package com.example.erjohnandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.BusInfoTableItem
import com.example.erjohnandroid.database.Model.mPadUnitsTable

@Dao
interface mPadUnitsDao {

    @Insert
    fun insertMpadUnitsBUlk(entity:List<mPadUnitsTable>)

    @Query("SELECT * FROM mPadUnits")
    fun getMpadUnits(): List<mPadUnitsTable>


}