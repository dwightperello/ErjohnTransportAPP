package com.example.erjohnandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.LinesTable
import com.example.erjohnandroid.database.Model.PassengerTypeTable

@Dao
interface PassengerTypeDao {

    @Query("Select * from PassengerType")
    fun getPassengerTYpe():List<PassengerTypeTable>

    @Insert
    fun insertPassengerTypeBulk(entity:List<PassengerTypeTable>)

    @Query("DELETE FROM PassengerType")
    fun truncatePassengertype()
}