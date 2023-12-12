package com.example.erjohnandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.FareTable
import com.example.erjohnandroid.database.Model.LinesTable

@Dao
interface FareDao {

    @Insert
  suspend  fun insertFare(entity:FareTable)

    @Query("Select * from Fares")
  suspend fun getFares():FareTable
}