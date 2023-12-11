package com.example.erjohnandroid.database

import android.content.Context
import android.os.Environment
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.erjohnandroid.database.Model.TripTicketTable
import com.example.erjohnandroid.database.sdcard_dao.sd_TripticketDao
import java.io.File

@Database(entities = [TripTicketTable::class],version=1, exportSchema = false)
abstract class SDCARD_database:RoomDatabase() {

    abstract fun getdsTripticketDao(): sd_TripticketDao

    companion object{
        private  var dbInstance:SDCARD_database?= null
        var sd = Environment.getExternalStorageDirectory()
      //val dbPath = File("/data/data/com.example.erjohnandroid/files", "CopyErjohnDB.db")
        // val dbPath = File("/sdcard/Documents/files","CopyErjohnDB.db")
      // val dbPath = File("/storage/CC18-5A74/Documents/files","CopyErjohnDB.db")
       //val dbPath = File("/sdcard/Documents/sdfiles","CopyErjohnDB.db")
       //val dbPath = File("/storage/94DA-C25C/Documents/files","CopyErjohnDB.db")
       val dbPath= File("/storage/F50D-181D/Documents/files","CopyErjohnDB.db")
      //  val dbPath = File("/sdcard", "CopyErjohnDB.db")
        fun getAppDB(context: Context):SDCARD_database{
            if(dbInstance==null){
                dbInstance= Room.databaseBuilder<SDCARD_database>(
                    context.applicationContext,SDCARD_database::class.java, dbPath.absolutePath
                )
                    .allowMainThreadQueries()
                    .build()
            }
            return dbInstance!!
        }
    }
}