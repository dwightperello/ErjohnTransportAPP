package com.example.erjohnandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.IngressoTable
import com.example.erjohnandroid.database.Model.LinesTable

@Dao
interface IngressoDao {

    @Insert
    fun inserIngresso(entity:IngressoTable)

    @Query("Select * from Ingresso")
    fun getAllIngresso():List<IngressoTable>

    @Query("DELETE FROM Ingresso")
    fun truncateingresso()
}