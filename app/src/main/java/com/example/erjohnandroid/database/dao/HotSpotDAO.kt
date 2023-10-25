package com.example.erjohnandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.HotSpotsTable
import com.example.erjohnandroid.database.Model.LinesTable

@Dao
interface HotSpotDAO {

    @Query("Select * from HotSpots")
    fun getAllHotspots():List<HotSpotsTable>

    @Query("Select * from HotSpots where lineid = :lineid")
    fun getHotspots(lineid:Int):List<HotSpotsTable>

    @Insert
    fun insertAllHotSpots(entity:List<HotSpotsTable>)
}