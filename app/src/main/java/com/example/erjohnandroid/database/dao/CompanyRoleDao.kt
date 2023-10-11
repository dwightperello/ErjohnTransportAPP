package com.example.erjohnandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.CompaniesTable
import com.example.erjohnandroid.database.Model.CompanyRolesTable

@Dao
interface CompanyRoleDao {
    @Insert
    fun insertCompanyrrolBulk(entity:List<CompanyRolesTable>)

    @Query("SELECT * FROM EmployeeRoles")
    fun getAllCompanyroles(): List<CompanyRolesTable>
}