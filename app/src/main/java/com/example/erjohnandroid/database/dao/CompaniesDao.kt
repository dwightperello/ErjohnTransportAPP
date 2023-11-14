package com.example.erjohnandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.BusInfoTableItem
import com.example.erjohnandroid.database.Model.CompaniesTable

@Dao
interface CompaniesDao {

    @Insert
    fun insertCompaniesBulk(entity:List<CompaniesTable>)

    @Query("SELECT * FROM Companies")
    fun getCompanies(): List<CompaniesTable>

    @Query("DELETE FROM Companies")
    fun truncateCopaniesDao()
}