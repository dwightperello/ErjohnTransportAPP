package com.example.erjohnandroid.database.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Terminals")
data class TerminalTable(
    @PrimaryKey(autoGenerate = true) val TerminalId: Int,
    val description: String,
    val id: Int,
    val name: String
)
