package com.example.erjohnandroid.util

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {
    val MIGRATION_1_2: Migration = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS 'temptable' (" +
                        "'id' INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "'amount' REAL)"
            );
        }
    }
    val MIGRATION_2_3: Migration = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS 'temptabletwo' (" +
                        "'id' INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "'amount' REAL)"
            );
        }
    }

    val MIGRATION_3_4: Migration = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS 'FareByKm' (" +
                        "amount Integer, " +
                        "upperkmlimit INTEGER, " +
                        "farekmId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "lineid INTEGER, " +
                        "lowerkmlimit INTEGER, " +
                        "totalkm INTEGER, " +
                        "id INTEGER, " +
                        "discountrate Integer)"





            )
        }
    }

}