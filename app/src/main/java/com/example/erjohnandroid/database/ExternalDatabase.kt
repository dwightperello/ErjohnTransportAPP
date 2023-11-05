package com.example.erjohnandroid.database

import android.content.Context
import android.os.Environment
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.erjohnandroid.database.Model.TicketCounterTable
import com.example.erjohnandroid.database.dao.ExternalDBDao


import java.io.File

@Database(entities = [TicketCounterTable::class],version=1, exportSchema = false)
abstract class externalDatabase: RoomDatabase() {

    abstract fun getexternalTicketCounter(): ExternalDBDao

    companion object{
        private  var dbInstance: externalDatabase?= null
        var sd = Environment.getExternalStorageDirectory()
        val dbPath = File("/sdcard/Documents/files","ExternalDB.db")




        fun getAppDB(context: Context): externalDatabase {
            if(dbInstance ==null){
                dbInstance = Room.databaseBuilder<externalDatabase>(
                    context.applicationContext, externalDatabase::class.java, dbPath.absolutePath
                )
                    .allowMainThreadQueries()
                    .build()
            }
            return dbInstance!!
        }
    }
}