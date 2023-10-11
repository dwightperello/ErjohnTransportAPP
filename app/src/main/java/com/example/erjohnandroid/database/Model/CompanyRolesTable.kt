package com.example.erjohnandroid.database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "EmployeeRoles")
data class CompanyRolesTable(
    @PrimaryKey(autoGenerate = true) val EmployeeroleId: Int,
    val id: Int?,
    val name: String?,
    val tag: Int?
)
