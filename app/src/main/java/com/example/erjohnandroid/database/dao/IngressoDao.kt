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

    @Query("Select * from Ingresso where ingressoRefId = :refid")
    fun getAllIngresso(refid:Int):List<IngressoTable>

    @Query("Select DISTINCT ingressoRefId from Ingresso")
    fun getAllIngressoreif():List<Int>

    @Query("DELETE FROM Ingresso")
    fun truncateingresso()

    @Query("SELECT COUNT(DISTINCT ingressoRefId) FROM Ingresso")
    fun countDistinctRefIDs(): Int
}