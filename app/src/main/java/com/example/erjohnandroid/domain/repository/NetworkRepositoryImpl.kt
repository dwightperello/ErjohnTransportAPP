package com.example.erjohnandroid.domain.repository

import com.example.erjohnandroid.domain.model.request.*
import com.example.erjohnandroid.domain.model.response.*
import com.example.erjohnandroid.domain.network.NetworkService
import com.example.erjohnandroid.util.ResultState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import javax.inject.Inject

class NetworkRepositoryImpl (private val networkService: NetworkService):NetworkRepository {

    override suspend fun Login(loginreqesut: request_login) : Flow<ResultState<response_login>> = flow {
        emit(ResultState.Loading)
        try {
            val response = networkService.login(loginreqesut)
            emit(ResultState.Success(response))
        }
        catch (e: Exception) {
            emit(ResultState.Error(e))
        }
    }

    override suspend fun getAllLines(token: String): Flow<ResultState<ArrayList <LinesItem>>> = flow{
        emit(ResultState.Loading)
        try {
            val response = networkService.getAllLines(token)
            emit(ResultState.Success(response))
        }catch (e:Exception) {
            emit((ResultState.Error(e)))
        }
    }

    override suspend fun getCompanies(token: String): Flow<ResultState<ArrayList<CompaniesItem>>> = flow {
        emit(ResultState.Loading)
        try {
            val response = networkService.getCompanies(token)
            emit(ResultState.Success(response))
        }catch (e:Exception) {
            emit((ResultState.Error(e)))
        }
    }

    override suspend fun getBusinfo(token: String): Flow<ResultState<ArrayList<BusInfos>>> = flow {
        emit(ResultState.Loading)
        try {
            val response = networkService.getBusInfo(token)
            emit(ResultState.Success(response))
        }catch (e:Exception) {
            emit((ResultState.Error(e)))
        }
    }

    override suspend fun getCompanyRole(token: String): Flow<ResultState<ArrayList<CompanyRolesItem>>> = flow {
        emit(ResultState.Loading)
        try {
            val response = networkService.getCompanyRoles(token)
            emit(ResultState.Success(response))
        }catch (e:Exception) {
            emit((ResultState.Error(e)))
        }
    }

    override suspend fun getExpensesType(token: String): Flow<ResultState<ArrayList<ExpensesTypesItem>>> = flow{
        emit(ResultState.Loading)
        try {
            val response = networkService.getExpensestype(token)
            emit(ResultState.Success(response))
        }catch (e:Exception) {
            emit((ResultState.Error(e)))
        }
    }

    override suspend fun getPassengerType(token: String): Flow<ResultState<ArrayList<PassengerTypeItem>>> = flow{
        emit(ResultState.Loading)
        try {
            val response = networkService.getPassengerTYpe(token)
            emit(ResultState.Success(response))
        }catch (e:Exception) {
            emit((ResultState.Error(e)))
        }
    }

    override suspend fun getWitholdingType(token: String): Flow<ResultState<ArrayList<WitholdingTypesItem>>> = flow {
        emit(ResultState.Loading)
        try {
            val response = networkService.getWitholdingTYpe(token)
            emit(ResultState.Success(response))
        }catch (e:Exception) {
            emit((ResultState.Error(e)))
        }
    }

    override suspend fun postIngresso(
        token: String,
        ingresso:List<Ingresso>
    ): Flow<ResultState<ResponseBody>> = flow {
        emit(ResultState.Loading)
        try {
            val response = networkService.postIngresso(token,ingresso)
            emit(ResultState.Success(response))
        }catch (e:Exception) {
            emit((ResultState.Error(e)))
        }
    }

    override suspend fun postInspection(
        token: String,
        inspectionReports:List<InspectionReports>
    ): Flow<ResultState<ResponseBody>> = flow{
        emit(ResultState.Loading)
        try {
            val response = networkService.postInspection(token, inspectionReports)
            emit(ResultState.Success(response))
        }catch (e:Exception) {
            emit((ResultState.Error(e)))
        }
    }

    override suspend fun postmpadAssignments(
        token: String,
        mPadAssignments:List<mPadAssignments>
    ): Flow<ResultState<ResponseBody>> = flow {
        emit(ResultState.Loading)
        try {
            val response = networkService.postmpadAssignments(token,mPadAssignments)
            emit(ResultState.Success(response))
        }catch (e:Exception) {
            emit((ResultState.Error(e)))
        }
    }

    override suspend fun postPartialRemits(
        token: String,
        partialRemit:List<PartialRemit>
    ): Flow<ResultState<ResponseBody>> = flow{
        emit(ResultState.Loading)
        try {
            val response = networkService.postPartialRemits(token,partialRemit)
            emit(ResultState.Success(response))
        }catch (e:Exception) {
            emit((ResultState.Error(e)))
        }
    }

    override suspend fun postTripcosts(
        token: String,
        tripCost:List<TripCost>
    ): Flow<ResultState<ResponseBody>> = flow{
        emit(ResultState.Loading)
        try {
            val response = networkService.postTripcosts(token,tripCost)
            emit(ResultState.Success(response))
        }catch (e:Exception) {
            emit((ResultState.Error(e)))
        }
    }

    override suspend fun posttripticketBULK(
        token: String,
        tripTIcket: List<TripTIcket>
    ): Flow<ResultState<ResponseBody>> = flow {
        emit(ResultState.Loading)
        try {
            val response = networkService.posttripticketBULK(token,tripTIcket)
            emit(ResultState.Success(response))
        }catch (e:Exception) {
            emit((ResultState.Error(e)))
        }
    }

    override suspend fun postWitholdingsBULK(
        token: String,
        tripWitholdings: List<TripWitholdings>
    ): Flow<ResultState<ResponseBody>> = flow {
        emit(ResultState.Loading)
        try {
            val response = networkService.postWitholdingsBULK(token,tripWitholdings)
            emit(ResultState.Success(response))
        }catch (e:Exception) {
            emit((ResultState.Error(e)))
        }
    }
}