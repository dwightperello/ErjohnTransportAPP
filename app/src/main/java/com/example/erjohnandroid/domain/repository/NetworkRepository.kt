package com.example.erjohnandroid.domain.repository

import com.example.erjohnandroid.domain.model.request.*
import com.example.erjohnandroid.domain.model.response.*
import com.example.erjohnandroid.util.ResultState
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody


interface NetworkRepository {

    suspend fun Login(loginreqesut: request_login) : Flow<ResultState<response_login>>

    suspend fun getAllLines(token:String):Flow<ResultState<ArrayList<LinesItem>>>

    suspend fun getMpadUnits(token: String):Flow<ResultState<ArrayList<mPadUnitsItem>>>
    suspend fun getAllHotspots(token:String):Flow<ResultState<ArrayList<HotSpotItem>>>

    suspend fun getCompanies(token:String):Flow<ResultState<ArrayList<CompaniesItem>>>

    suspend fun getBusinfo(token:String):Flow<ResultState<ArrayList<BusInfos>>>

    suspend fun getCompanyRole(token: String):Flow<ResultState<ArrayList<CompanyRolesItem>>>

    suspend fun getExpensesType(token: String):Flow<ResultState<ArrayList<ExpensesTypesItem>>>

    suspend fun getPassengerType(token: String):Flow<ResultState<ArrayList<PassengerTypeItem>>>

    suspend fun getWitholdingType(token: String):Flow<ResultState<ArrayList<WitholdingTypesItem>>>

    suspend fun getTerminals(token: String):Flow<ResultState<ArrayList<TerminalsItem>>>

    suspend fun getFares(token: String,Id:Int):Flow<ResultState<Fares>>

    //SYNCHING

    suspend fun postIngresso(token: String,ingresso:List<Ingresso>) : Flow<ResultState<ResponseBody>>

    suspend fun postTripReverse(token: String,tripreverse:List<TripReverseItem>) : Flow<ResultState<ResponseBody>>

    suspend fun postIngressoALL(token: String,ingresso:postAllItem) : Flow<ResultState<ResponseBody>>

    suspend fun postInspection(token: String,inspectionReports:List<InspectionReports>) : Flow<ResultState<ResponseBody>>

    suspend fun postmpadAssignments(token: String,mPadAssignments:List<mPadAssignments>) : Flow<ResultState<ResponseBody>>

    suspend fun postPartialRemits(token: String,partialRemit:List<PartialRemit>) : Flow<ResultState<ResponseBody>>

    suspend fun postTripcosts(token: String,tripCost:List<TripCost>) : Flow<ResultState<ResponseBody>>

    suspend fun posttripticketBULK(token: String,tripTIcket:List<TripTIcket>) : Flow<ResultState<ResponseBody>>

    suspend fun postWitholdingsBULK(token: String,tripWitholdings:List<TripWitholdings>) : Flow<ResultState<ResponseBody>>


}