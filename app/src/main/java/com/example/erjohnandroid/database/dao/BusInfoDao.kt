package com.example.erjohnandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.BusInfoTableItem

@Dao
interface BusInfoDao {

    @Insert
   suspend fun insertBusinfoBulk(entity:List<BusInfoTableItem>)

    @Query("SELECT * FROM BusInfo where companyId = :id")
   suspend fun getBusinfo(id:Int): List<BusInfoTableItem>

    @Query("DELETE FROM BusInfo")
   suspend fun truncateBusinfo()
}