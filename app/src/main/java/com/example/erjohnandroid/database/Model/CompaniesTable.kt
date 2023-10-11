package com.example.erjohnandroid.database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Companies")
data class CompaniesTable (
    @PrimaryKey(autoGenerate = true) val CompaniesId: Int,
    val companyName: String?,
    val id: Int?,
    val remarks: String?,
    val tag: Int?
        )
