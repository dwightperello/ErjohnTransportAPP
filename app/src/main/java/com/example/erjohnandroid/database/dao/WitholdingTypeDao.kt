package com.example.erjohnandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.TripWitholdingTable
import com.example.erjohnandroid.database.Model.WitholdingTypeTable

@Dao
interface WitholdingTypeDao {

    @Query("Select * from WitholdingType")
    fun getWitholdingtype():List<WitholdingTypeTable>

    @Insert
    fun insertwitholdingtypeBUlk(entity:List<WitholdingTypeTable>)
}