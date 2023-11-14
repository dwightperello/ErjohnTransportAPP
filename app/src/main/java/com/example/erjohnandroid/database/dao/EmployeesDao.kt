package com.example.erjohnandroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.erjohnandroid.database.Model.BusInfoTableItem
import com.example.erjohnandroid.database.Model.EmployeesTable

@Dao
interface EmployeesDao {

    @Insert
    fun insertEmployeeBulk(entity:List<EmployeesTable>)

    @Query("SELECT * FROM Employees where companyRolesId = :id")
    fun getEmployees(id:Int): List<EmployeesTable>

    @Query("SELECT * FROM Employees where pin = :pin")
    fun selectEmployee(pin:Int): EmployeesTable

    @Query("SELECT * FROM Employees where companyRolesId = :id")
    fun selectConductor(id:Int):List< EmployeesTable>

    @Query("SELECT * FROM Employees where companyRolesId = :id")
    fun selectDriver(id:Int):List<EmployeesTable>

    @Query("DELETE FROM Employees")
    fun truncateEmployees()


}