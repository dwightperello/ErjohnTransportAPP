package com.example.erjohnandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.LinesTable

@Dao
interface LineDao {
    @Query("Select * from Lines")
    fun getAllLines():List<LinesTable>

    @Insert
    fun insertAllLines(entity:List<LinesTable>)

    @Query("DELETE FROM Lines")
    fun truncateLines()
}