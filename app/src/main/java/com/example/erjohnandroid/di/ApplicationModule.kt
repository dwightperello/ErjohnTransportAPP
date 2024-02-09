package com.example.erjohnandroid.di

import android.app.Application
import android.content.Context
import com.example.erjohnandroid.database.AppDatabase
import com.example.erjohnandroid.database.SDCARD_database
import com.example.erjohnandroid.database.dao.*
import com.example.erjohnandroid.database.externalDatabase
import com.example.erjohnandroid.database.repository.sd_Repository
import com.example.erjohnandroid.database.sdcard_dao.sd_TripticketDao
import com.example.erjohnandroid.domain.network.NetworkBuilder
import com.example.erjohnandroid.domain.network.NetworkService
import com.example.erjohnandroid.domain.repository.NetworkRepositoryImpl
import com.example.erjohnandroid.util.GlobalVariable
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    @Singleton
    fun provideNetworkService(@ApplicationContext context: Context): NetworkService {
        return NetworkBuilder.create(
            GlobalVariable.API_BASE_URL,
            context.cacheDir,
            (10 * 1024 * 1024).toLong()
        )
    }

    @Provides
    @Singleton
    fun provideTaskRepository(
        networkService: NetworkService,
    ): NetworkRepositoryImpl {
        return NetworkRepositoryImpl(
            networkService
        )
    }

    @Provides
    @Singleton
    fun ProvideAppDatabase(context: Application):AppDatabase{
        return AppDatabase.getAppDB(context)
    }

    @Provides
    @Singleton
    fun Providesd(context: Application):SDCARD_database{
        return SDCARD_database.getAppDB(context)
    }

    @Provides
    @Singleton
    fun ProvidesExternalDB(context: Application):externalDatabase{
        return externalDatabase.getAppDB(context)
    }

    @Provides
    @Singleton
    fun ProvideLine(appdb: AppDatabase):LineDao{
        return appdb.getLineDao()
    }



    @Provides
    @Singleton
    fun ProvideLinesegment(appdb: AppDatabase):LineSegmentDao{
        return appdb.getLinesegmentdao()
    }

    @Provides
    @Singleton
    fun Providebusinfo(appdb: AppDatabase):BusInfoDao{
        return appdb.getBusinfoDao()
    }

    @Provides
    @Singleton
    fun Providecompanies(appdb: AppDatabase):CompaniesDao{
        return appdb.getCompaniesDao()
    }

    @Provides
    @Singleton
    fun Providecompaniesroles(appdb: AppDatabase):CompanyRoleDao{
        return appdb.getCompaniesRoleDao()
    }

    @Provides
    @Singleton
    fun Provideemployees(appdb: AppDatabase):EmployeesDao{
        return appdb.getEmployeeDao()
    }

    @Provides
    @Singleton
    fun ProvideexpensesType(appdb: AppDatabase):ExpensesTypeDao{
        return appdb.getExpensestypeDao()
    }

    @Provides
    @Singleton
    fun ProvideInspectionreport(appdb: AppDatabase):InspectionReportDao{
        return appdb.getInspectionReportDao()
    }

    @Provides
    @Singleton
    fun Providempadassignment(appdb: AppDatabase):mPadAssignmentsDao{
        return appdb.getmPadAssignmentDao()
    }



    @Provides
    @Singleton
    fun ProvidePartialremit(appdb: AppDatabase):PartialRemitDao{
        return appdb.getPartialRemitDao()
    }

    @Provides
    @Singleton
    fun Providepassengertype(appdb: AppDatabase):PassengerTypeDao{
        return appdb.getPassengertypeDao()
    }

    @Provides
    @Singleton
    fun ProvideTripCost(appdb: AppDatabase):TripCostDao{
        return appdb.getTripCoastDao()
    }

    @Provides
    @Singleton
    fun ProvideTripticket(appdb: AppDatabase):TripTicketDao{
        return appdb.getTripticketDao()
    }

    @Provides
    @Singleton
    fun ProvidesdTripticket(appdb: SDCARD_database):sd_TripticketDao{
        return appdb.getdsTripticketDao ()
    }

    @Provides
    @Singleton
    fun ProvidesexternalDatabase(appdb: externalDatabase):ExternalDBDao{
        return appdb.getexternalTicketCounter ()
    }

    @Provides
    @Singleton
    fun ProvidesSavedDispatch(appdb: externalDatabase):SavedDispatchedDao{
        return appdb.getSaveDispatchInfo ()
    }

    @Provides
    @Singleton
    fun ProvideTripwitholding(appdb: AppDatabase):TripWitholdingDao{
        return appdb.getTripwitholdingtDao()
    }

    @Provides
    @Singleton
    fun ProvideWitholdingtype(appdb: AppDatabase):WitholdingTypeDao{
        return appdb.getWitholdingtypetDao()
    }

    @Provides
    @Singleton
    fun ProvidesynchTripticket(appdb: AppDatabase):Synch_TripticketDao{
        return appdb.getsynchtripticketdao()
    }

    @Provides
    @Singleton
    fun Providesynchinspectionreport(appdb: AppDatabase):Synch_InspectionReportDao{
        return appdb.getsynchinspectionreportdao()
    }

    @Provides
    @Singleton
    fun Providesynchinmpadassignment(appdb: AppDatabase):Synch_mPadAssignmentDao{
        return appdb.getsynchmpadassignmentDao()
    }

    @Provides
    @Singleton
    fun Providesynchinpartialremit(appdb: AppDatabase):Synch_PartialRemitDao{
        return appdb.getsynchpartialremitDao()
    }

    @Provides
    @Singleton
    fun Providesynchintripcost(appdb: AppDatabase):Synch_TripcostDao{
        return appdb.getsynchTripCostDao()
    }

    @Provides
    @Singleton
    fun Providesynchintripwithodling(appdb: AppDatabase):Synch_TripwitholdingDao{
        return appdb.getsynchTripwithodingDao()
    }

    @Provides
    @Singleton
    fun ProvideIngresso(appdb: AppDatabase):IngressoDao{
        return appdb.getIngresoDao()
    }

    @Provides
    @Singleton
    fun ProvideHotspot(appdb: AppDatabase):HotSpotDAO{
        return appdb.getHotspotDao()
    }

    @Provides
    @Singleton
    fun ProvideTicketnumber(appdb: AppDatabase):TicketNumDAO{
        return appdb.getTicketnumberDao()
    }

    @Provides
    @Singleton
    fun ProvidempadUnits(appdb: AppDatabase):mPadUnitsDao{
        return appdb.getMpadunitsDao()
    }

    @Provides
    @Singleton
    fun ProvideTerminal(appdb: AppDatabase):TerminalDao{
        return appdb.getTerminalsDao()
    }

    @Provides
    @Singleton
    fun ProvideTripreverse(appdb: AppDatabase):TripReverseDao{
        return appdb.getTripReverseDao()
    }

    @Provides
    @Singleton
    fun ProvidesynchTripreverse(appdb: AppDatabase):Synch_TripReverseDao{
        return appdb.getsynchTripReversetDao()
    }

    @Provides
    @Singleton
    fun ProvidesLogreport(appdb: AppDatabase):LogReportDao{
        return appdb.getLogReportDao()
    }

    @Provides
    @Singleton
    fun ProvidesynchLogReport(appdb: AppDatabase):Synch_LogReportDao{
        return appdb.getsynchLogReportDao()
    }

    @Provides
    @Singleton
    fun ProvidesFare(appdb: AppDatabase):FareDao{
        return appdb.getFaredao()
    }

    @Provides
    @Singleton
    fun ProvidesFarebykm(appdb: AppDatabase):farebykmDao{
        return appdb.getFarebykmDao()
    }



}