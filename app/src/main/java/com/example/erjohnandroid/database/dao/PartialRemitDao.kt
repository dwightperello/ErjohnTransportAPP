package com.example.erjohnandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.PartialRemitTable

@Dao
interface PartialRemitDao {

    @Insert
    fun insertPartialremit(entity:PartialRemitTable)

    @Query("SELECT * FROM PartialRemit where ingressoRefId = :refid")
    fun getPartialRemit(refid:Int): List<PartialRemitTable>

    @Query("DELETE FROM PartialRemit")
    fun truncatepartial()
}