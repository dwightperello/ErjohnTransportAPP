package com.example.erjohnandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.BusInfoTableItem
import com.example.erjohnandroid.database.Model.TerminalTable

@Dao
interface TerminalDao {

    @Insert
    fun insertTerminalBulk(entity:List<TerminalTable>)

    @Query("SELECT * FROM Terminals")
    fun getTerminals(): List<TerminalTable>

    @Query("DELETE FROM Terminals")
    fun truncateTerminals()
}