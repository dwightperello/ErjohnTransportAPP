package com.example.erjohnandroid.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.erjohnandroid.database.Model.*
import com.example.erjohnandroid.database.dao.*
import com.example.erjohnandroid.util.Migrations

@Database(entities = [LinesTable::class,LineSegmentTable::class,BusInfoTableItem::class,
    CompaniesTable::class,CompanyRolesTable::class,EmployeesTable::class,
    ExpensesTypeTable::class,InspectionReportTable::class,mPadAssignmentsTable::class,PassengerTypeTable::class,
    TripCostTable::class,TripTicketTable::class,TripWitholdingTable::class,WitholdingTypeTable::class,HotSpotsTable::class,TicketCounterTable::class,
    PartialRemitTable::class,Sycn_TripticketTable::class,Sycnh_InspectionReportTable::class,Synch_mpadAssignmentsTable::class,Synch_partialremitTable::class,Synch_TripCostTable::class,Synch_TripwitholdingTable::class, IngressoTable::class,mPadUnitsTable::class,TerminalTable::class,TripReverseTable::class,Synch_TripReverseTable::class,LogReport::class,Synch_LogReport::class,FareTable::class,FareByKm::class], version = 4, exportSchema = false)
abstract class AppDatabase:RoomDatabase() {

    abstract fun getLineDao(): LineDao
    abstract fun getMpadunitsDao(): mPadUnitsDao
    abstract fun getHotspotDao(): HotSpotDAO
    abstract fun getLinesegmentdao(): LineSegmentDao
    abstract fun getBusinfoDao():BusInfoDao
    abstract fun getCompaniesDao():CompaniesDao
    abstract fun getCompaniesRoleDao():CompanyRoleDao
    abstract fun getEmployeeDao():EmployeesDao
    abstract fun getExpensestypeDao():ExpensesTypeDao
    abstract fun getInspectionReportDao():InspectionReportDao
    abstract fun getmPadAssignmentDao():mPadAssignmentsDao
    abstract fun getPassengertypeDao():PassengerTypeDao
    abstract fun getTripCoastDao():TripCostDao
    abstract fun getTripticketDao():TripTicketDao
    abstract fun getTripwitholdingtDao():TripWitholdingDao
    abstract fun getWitholdingtypetDao():WitholdingTypeDao
    abstract fun getPartialRemitDao():PartialRemitDao
    abstract fun getsynchtripticketdao():Synch_TripticketDao
    abstract fun getsynchinspectionreportdao():Synch_InspectionReportDao
    abstract fun getsynchmpadassignmentDao():Synch_mPadAssignmentDao
    abstract fun getsynchpartialremitDao():Synch_PartialRemitDao

    abstract fun getsynchTripReversetDao():Synch_TripReverseDao

    abstract fun getTerminalsDao():TerminalDao
    abstract fun getTripReverseDao():TripReverseDao
    abstract fun getsynchTripCostDao():Synch_TripcostDao
    abstract fun getsynchTripwithodingDao():Synch_TripwitholdingDao
    abstract fun getIngresoDao():IngressoDao

    abstract fun getTicketnumberDao():TicketNumDAO

    abstract fun getLogReportDao():LogReportDao
    abstract fun getsynchLogReportDao():Synch_LogReportDao

    abstract fun getFaredao():FareDao
    abstract fun getFarebykmDao():farebykmDao

    companion object{
        private  var dbInstance:AppDatabase?= null

        fun getAppDB(context: Context):AppDatabase{
            if(dbInstance==null){
                dbInstance= Room.databaseBuilder<AppDatabase>(
                    context.applicationContext,AppDatabase::class.java,"ErjohnDB"
                )
                    .addMigrations(Migrations.MIGRATION_1_2)
                    .addMigrations(Migrations.MIGRATION_2_3)
                    .addMigrations(Migrations.MIGRATION_3_4)

                    .allowMainThreadQueries()
                    .build()
            }
            return dbInstance!!
        }
    }
}