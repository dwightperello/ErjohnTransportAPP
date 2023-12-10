package com.example.erjohnandroid.presenter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.erjohnandroid.database.Model.HotSpotsTable
import com.example.erjohnandroid.domain.model.request.*
import com.example.erjohnandroid.domain.model.response.*
import com.example.erjohnandroid.domain.repository.NetworkRepositoryImpl
import com.example.erjohnandroid.util.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class networkViewModel @Inject constructor (private val networkRepositoryImpl: NetworkRepositoryImpl) : ViewModel() {

    private var _login: MutableLiveData<ResultState<response_login>> = MutableLiveData()
    val login: LiveData<ResultState<response_login>> get() = _login

    private  var _allLines:MutableLiveData<ResultState<ArrayList<LinesItem>>> = MutableLiveData()
    val allLines:LiveData<ResultState<ArrayList<LinesItem>>> get() = _allLines

    private  var _allTerminals:MutableLiveData<ResultState<ArrayList<TerminalsItem>>> = MutableLiveData()
    val allTerminals:LiveData<ResultState<ArrayList<TerminalsItem>>> get() = _allTerminals

    private  var _mpadunits:MutableLiveData<ResultState<ArrayList<mPadUnitsItem>>> = MutableLiveData()
    val mpadunits:LiveData<ResultState<ArrayList<mPadUnitsItem>>> get() = _mpadunits

    private  var _allHotspots:MutableLiveData<ResultState<ArrayList<HotSpotItem>>> = MutableLiveData()
    val allHotspots:LiveData<ResultState<ArrayList<HotSpotItem>>> get() = _allHotspots

    private  var _companies:MutableLiveData<ResultState<ArrayList<CompaniesItem>>> = MutableLiveData()
    val companies:LiveData<ResultState<ArrayList<CompaniesItem>>> get() = _companies

    private  var _businfo:MutableLiveData<ResultState<ArrayList<BusInfos>>> = MutableLiveData()
    val businfo:LiveData<ResultState<ArrayList<BusInfos>>> get() = _businfo

    private  var _companyroles:MutableLiveData<ResultState<ArrayList<CompanyRolesItem>>> = MutableLiveData()
    val companyroles:LiveData<ResultState<ArrayList<CompanyRolesItem>>> get() = _companyroles

    private  var _expensestype:MutableLiveData<ResultState<ArrayList<ExpensesTypesItem>>> = MutableLiveData()
    val expensestype:LiveData<ResultState<ArrayList<ExpensesTypesItem>>> get() = _expensestype

    private  var _passengertype:MutableLiveData<ResultState<ArrayList<PassengerTypeItem>>> = MutableLiveData()
    val passengertype:LiveData<ResultState<ArrayList<PassengerTypeItem>>> get() = _passengertype

    private  var _witholdingtype:MutableLiveData<ResultState<ArrayList<WitholdingTypesItem>>> = MutableLiveData()
    val witholdingtype:LiveData<ResultState<ArrayList<WitholdingTypesItem>>> get() = _witholdingtype

    private var _postingresso:MutableLiveData<ResultState<ResponseBody>> = MutableLiveData()
    val postingresso:LiveData<ResultState<ResponseBody>> get() = _postingresso

    private var _postinspection:MutableLiveData<ResultState<ResponseBody>> = MutableLiveData()
    val postinspection:LiveData<ResultState<ResponseBody>> get() = _postinspection

    private var _postmpadassignments:MutableLiveData<ResultState<ResponseBody>> = MutableLiveData()
    val postmpadassignments:LiveData<ResultState<ResponseBody>> get() = _postmpadassignments

    private var _postpartialremit:MutableLiveData<ResultState<ResponseBody>> = MutableLiveData()
    val postpartialremit:LiveData<ResultState<ResponseBody>> get() = _postpartialremit

    private var _posttripcosts:MutableLiveData<ResultState<ResponseBody>> = MutableLiveData()
    val posttripcosts:LiveData<ResultState<ResponseBody>> get() = _posttripcosts

    private var _posttripticketBULK:MutableLiveData<ResultState<ResponseBody>> = MutableLiveData()
    val posttripticketBULK:LiveData<ResultState<ResponseBody>> get() = _posttripticketBULK

    private var _postwitholdingBULK:MutableLiveData<ResultState<ResponseBody>> = MutableLiveData()
    val postwitholdingBULK:LiveData<ResultState<ResponseBody>> get() = _postwitholdingBULK

    private var _postIngressoALL:MutableLiveData<ResultState<ResponseBody>> = MutableLiveData()
    val postIngressoALL:LiveData<ResultState<ResponseBody>> get() = _postIngressoALL

    private var _postTripReverse:MutableLiveData<ResultState<ResponseBody>> = MutableLiveData()
    val postTripReverse:LiveData<ResultState<ResponseBody>> get() = _postTripReverse



    fun login(loginCredentials: request_login){
        viewModelScope.launch(Dispatchers.IO) {
            networkRepositoryImpl.Login(loginCredentials)
                .onEach {
                    _login.value=it
                }
                .launchIn(viewModelScope)
        }
    }

    fun getAllLines(token:String){
        viewModelScope.launch(Dispatchers.IO) {
            networkRepositoryImpl.getAllLines("Bearer ${token}")
                .onEach { _allLines.value=it }.launchIn(viewModelScope)
        }
    }

    fun getAllTerminals(token:String){
        viewModelScope.launch(Dispatchers.IO) {
            networkRepositoryImpl.getTerminals("Bearer ${token}")
                .onEach { _allTerminals.value=it }.launchIn(viewModelScope)
        }
    }

    fun getMpadUnits(token:String){
        viewModelScope.launch(Dispatchers.IO) {
            networkRepositoryImpl.getMpadUnits("Bearer ${token}")
                .onEach { _mpadunits.value=it }.launchIn(viewModelScope)
        }
    }

    fun getAllHotspots(token:String){
        viewModelScope.launch(Dispatchers.IO) {
            networkRepositoryImpl.getAllHotspots("Bearer ${token}")
                .onEach { _allHotspots.value=it }.launchIn(viewModelScope)
        }
    }

    fun getCompanies(token: String){
        viewModelScope.launch(Dispatchers.IO) {
            networkRepositoryImpl.getCompanies("Bearer ${token}")
                .onEach { _companies.value=it }.launchIn(viewModelScope)
        }
    }

    fun getBusinfo(token: String){
        viewModelScope.launch(Dispatchers.IO) {
            networkRepositoryImpl.getBusinfo("Bearer ${token}")
                .onEach { _businfo.value=it }.launchIn(viewModelScope)
        }
    }

    fun getCompanyRoles(token: String){
        viewModelScope.launch(Dispatchers.IO) {
            networkRepositoryImpl.getCompanyRole("Bearer ${token}")
                .onEach { _companyroles.value=it }.launchIn(viewModelScope)
        }
    }

    fun getExpensesTYpe(token: String){
        viewModelScope.launch(Dispatchers.IO) {
            networkRepositoryImpl.getExpensesType("Bearer ${token}")
                .onEach { _expensestype.value=it }.launchIn(viewModelScope)
        }
    }

    fun getPassengerType(token: String){
        viewModelScope.launch(Dispatchers.IO) {
            networkRepositoryImpl.getPassengerType("Bearer ${token}")
                .onEach { _passengertype.value=it }.launchIn(viewModelScope)
        }
    }

    fun getWitholdingType(token: String){
        viewModelScope.launch(Dispatchers.IO) {
            networkRepositoryImpl.getWitholdingType("Bearer ${token}")
                .onEach { _witholdingtype.value=it }.launchIn(viewModelScope)
        }
    }

    //region SYNCHING
    fun postIngresso(token: String,ingresso:List<Ingresso>){
        viewModelScope.launch(Dispatchers.IO) {
            networkRepositoryImpl.postIngresso("Bearer ${token}",ingresso)
                .onEach { _postingresso.value=it }.launchIn(viewModelScope)
        }
    }

    fun postIngressoALL(token: String,ingresso:postAllItem){
        viewModelScope.launch(Dispatchers.IO) {
            networkRepositoryImpl.postIngressoALL("Bearer ${token}",ingresso)
                .onEach { _postIngressoALL.value=it }.launchIn(viewModelScope)
        }
    }



    fun postInspection(token: String,inspectionReports:List<InspectionReports>){
        viewModelScope.launch(Dispatchers.IO) {
            networkRepositoryImpl.postInspection("Bearer ${token}",inspectionReports)
                .onEach { _postinspection.value=it }.launchIn(viewModelScope)
        }
    }

    fun postmpadAssignments(token: String,mPadAssignments:List<mPadAssignments>){
        viewModelScope.launch(Dispatchers.IO) {
            networkRepositoryImpl.postmpadAssignments("Bearer ${token}",mPadAssignments)
                .onEach { _postmpadassignments.value=it }.launchIn(viewModelScope)
        }
    }

    fun postPartialRemits(token: String,partialRemit:List<PartialRemit>){
        viewModelScope.launch(Dispatchers.IO) {
            networkRepositoryImpl.postPartialRemits("Bearer ${token}",partialRemit)
                .onEach { _postpartialremit.value=it }.launchIn(viewModelScope)
        }
    }

    fun postTripReverseALL(token: String,tripreverse:List<TripReverseItem>){
        viewModelScope.launch(Dispatchers.IO) {
            networkRepositoryImpl.postTripReverse("Bearer ${token}",tripreverse)
                .onEach { _postTripReverse.value=it }.launchIn(viewModelScope)
        }
    }

    fun postTripcosts(token: String,tripCost:List<TripCost>){
        viewModelScope.launch(Dispatchers.IO) {
            networkRepositoryImpl.postTripcosts("Bearer ${token}",tripCost)
                .onEach { _posttripcosts.value=it }.launchIn(viewModelScope)
        }
    }

    fun posttripticketBULK(token: String,tripTIcket:List<TripTIcket>){
        viewModelScope.launch(Dispatchers.IO) {
            networkRepositoryImpl.posttripticketBULK("Bearer ${token}",tripTIcket)
                .onEach { _posttripticketBULK.value=it }.launchIn(viewModelScope)
        }
    }

    fun postWitholdingsBULK(token: String,tripWitholdings:List<TripWitholdings>){
        viewModelScope.launch(Dispatchers.IO) {
            networkRepositoryImpl.postWitholdingsBULK("Bearer ${token}",tripWitholdings)
                .onEach { _postwitholdingBULK.value=it }.launchIn(viewModelScope)
        }
    }
    //endregion
}