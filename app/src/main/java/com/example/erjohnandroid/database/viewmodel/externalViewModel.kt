package com.example.erjohnandroid.database.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Query
import com.example.erjohnandroid.database.Model.TicketCounterTable
import com.example.erjohnandroid.database.Model.externalDispatch.SavedDispatchInfo
import com.example.erjohnandroid.database.repository.RoomRepository
import com.example.erjohnandroid.database.repository.externalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class externalViewModel @Inject constructor(private val externalrepository: externalRepository): ViewModel() {

    private  var _ticketnumberstart: MutableLiveData<TicketCounterTable> = MutableLiveData()
    val ticketnumberstart: LiveData<TicketCounterTable> = _ticketnumberstart


    private  var _savedDispatch: MutableLiveData<SavedDispatchInfo> = MutableLiveData()
    val savedDispatch: LiveData<SavedDispatchInfo> = _savedDispatch

    fun getTicketnumber(){
        viewModelScope.launch() {
            val records=  externalrepository.getTicketnumbers()
            _ticketnumberstart.value=records
        }
    }

    fun inserticketnu(entity: TicketCounterTable){
        viewModelScope.launch() {
            val records=  externalrepository.insertticketnum(entity)

        }
    }

    fun updateTicketnumber(ticketnumber:Int,refid: Int, id: Int){
        viewModelScope.launch() {
            val records=  externalrepository.updateTicketnumbers(ticketnumber,refid,id)

        }
    }


    fun getSavedDispatch(){
        viewModelScope.launch() {
            val records=  externalrepository.getSavedDispatch()
            _savedDispatch.value=records
        }
    }

    fun insertSavedDispatched(entity: SavedDispatchInfo){
        viewModelScope.launch() {
            val records=  externalrepository.insertSavedDispatch(entity)

        }
    }

    fun updateSavedDispatched(busnumber: String,conductorName: String,isDispatched: Boolean,dispatcherName: String,driverName: String,line: String,LineId: Int,mPadUnit: String,reverse:Int,orginalTicketnumber:Int,direction:String,ingressoRefId:Int,machinename:String,permitnumber:String,serialnumber:String){
        viewModelScope.launch() {
            try {
                val records=  externalrepository.updateSavedDispatched(busnumber,conductorName,isDispatched,dispatcherName,driverName,line, LineId,mPadUnit,reverse,orginalTicketnumber,direction,ingressoRefId,machinename,permitnumber,serialnumber)
            }catch (e:Exception){
                Log.e("erro",e.localizedMessage)
            }


        }
    }

    fun updateSavedDispatchedReverse(direction:String, reverse: Int){
        viewModelScope.launch() {
            val records=  externalrepository.updateSavedDispatchedReverse(direction,reverse)

        }
    }

    fun updateReverseonly(reverse: Int){
        viewModelScope.launch() {
            val records=  externalrepository.updateReverseOnly(reverse)

        }
    }

    fun updateIsDispathced(isDispatched: Boolean){
        viewModelScope.launch() {
            val records=  externalrepository.updateIsDispatched(isDispatched)

        }
    }

}