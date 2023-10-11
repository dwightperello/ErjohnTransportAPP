package com.example.erjohnandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.CompaniesTable
import com.example.erjohnandroid.database.Model.ExpensesTypeTable

@Dao
interface ExpensesTypeDao {

    @Insert
    fun insertExpensesTypeBulk(entity:List<ExpensesTypeTable>)

    @Query("SELECT * FROM ExpensesTypes")
    fun getExpensesStype(): List<ExpensesTypeTable>
}