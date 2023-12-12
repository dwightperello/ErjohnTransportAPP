package com.example.erjohnandroid.domain.network

import com.example.erjohnandroid.domain.model.request.*
import com.example.erjohnandroid.domain.model.response.*
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface NetworkService {

    @POST("Account/login")
    suspend fun login(@Body loginreqesut: request_login): response_login

    @GET("Lines")
    suspend fun getAllLines(@Header("Authorization")token:String):ArrayList<LinesItem>

    @GET("Fares/{Id}")
    suspend fun getFares(@Header("Authorization")token:String,@Path("Id")Id:Int):Fares

    @GET("mPadUnits")
    suspend fun getMpadUnits(@Header("Authorization")token:String):ArrayList<mPadUnitsItem>

    @GET("Hotspots")
    suspend fun getAllHotspots(@Header("Authorization")token:String):ArrayList<HotSpotItem>

    @GET("companies")
    suspend fun getCompanies(@Header("Authorization")token:String):ArrayList<CompaniesItem>

    @GET("BusInfo")
    suspend fun getBusInfo(@Header("Authorization")token:String):ArrayList<BusInfos>

    @GET("CompanyRoles")
    suspend fun getCompanyRoles(@Header("Authorization")token:String):ArrayList<CompanyRolesItem>

    @GET("ExpensesTypes")
    suspend fun getExpensestype(@Header("Authorization")token:String):ArrayList<ExpensesTypesItem>

    @GET("PassengerTypes")
    suspend fun getPassengerTYpe(@Header("Authorization")token:String):ArrayList<PassengerTypeItem>

    @GET("WithholdingTypes")
    suspend fun getWitholdingTYpe(@Header("Authorization")token:String):ArrayList<WitholdingTypesItem>

    //SYNCHING FUNCTIONS
    @POST("Ingresso")
    suspend fun  postIngresso(@Header("Authorization")token:String,@Body ingresso:List<Ingresso>) : ResponseBody

    @POST("Ingresso")
    suspend fun  postIngressoALL(@Header("Authorization")token:String,@Body ingresso:postAllItem) : ResponseBody

    @POST("InspectionReports")
    suspend fun  postInspection(@Header("Authorization")token:String,@Body inspectionReports:List<InspectionReports>) : ResponseBody

    @POST("mPadAssignments")
    suspend fun  postmpadAssignments(@Header("Authorization")token:String,@Body mPadAssignments:List<mPadAssignments>) : ResponseBody

    @POST("PartialRemits")
    suspend fun  postPartialRemits(@Header("Authorization")token:String,@Body partialRemit:List<PartialRemit>) : ResponseBody

    @POST("TripCosts")
    suspend fun  postTripcosts(@Header("Authorization")token:String,@Body tripCost:List<TripCost>) : ResponseBody

    @POST("TripTickets/PostTripTicketBulk")
    suspend fun  posttripticketBULK(@Header("Authorization")token:String,@Body tripTIcket:List<TripTIcket>) : ResponseBody

    @POST("TripWitholdings/PostTripWitholdingBulk")
    suspend fun  postWitholdingsBULK(@Header("Authorization")token:String,@Body tripWitholdings:List<TripWitholdings>) : ResponseBody

    @GET("Terminals")
    suspend fun  getTerminals(@Header("Authorization")token:String) : ArrayList<TerminalsItem>

    @POST("TripReverse")
    suspend fun  postTripReverseBULK(@Header("Authorization")token:String,@Body tripreverse:List<TripReverseItem>) : ResponseBody
}