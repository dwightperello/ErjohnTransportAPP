package com.example.erjohnandroid.util

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {
    val MIGRATION_1_2: Migration = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS 'tasks' (" +
                        "'id' INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "'amount' REAL)"
            );
        }
    }
    val MIGRATION_2_3: Migration = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS 'testingtwo' (" +
                        "'id' INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "'amount' REAL)"
            );
        }
    }
}