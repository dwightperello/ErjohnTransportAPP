package com.example.erjohnandroid.database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ExpensesTypes")
data class ExpensesTypeTable(
    @PrimaryKey(autoGenerate = true) val ExpensesTypeId:Int,
    val id: Int?,
    val name: String?,
    val tag: Int?
)
