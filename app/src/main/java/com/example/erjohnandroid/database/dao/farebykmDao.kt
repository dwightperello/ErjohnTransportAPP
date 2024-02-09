package com.example.erjohnandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.FareByKm


@Dao
interface farebykmDao {

    @Query("Select * from FareByKm")
    fun getAllfarebykm():List<FareByKm>

    @Insert
    fun insertfarebykm(entity:List<FareByKm>)

    @Query("DELETE FROM FareByKm")
    fun truncateFarebykm()
}