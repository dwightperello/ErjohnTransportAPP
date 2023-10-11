package com.example.erjohnandroid.database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Employees")
data class EmployeesTable (
        @PrimaryKey(autoGenerate = true) val EmployeeId:Int,
        val companyRolesId: Int?,
        val id: Int?,
        val lastName: String?,
        val name: String?,
        val pin: Int?
        )
