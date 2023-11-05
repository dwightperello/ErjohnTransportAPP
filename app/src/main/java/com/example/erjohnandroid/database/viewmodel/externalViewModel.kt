package com.example.erjohnandroid.database.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.erjohnandroid.database.Model.TicketCounterTable
import com.example.erjohnandroid.database.repository.RoomRepository
import com.example.erjohnandroid.database.repository.externalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class externalViewModel @Inject constructor(private val externalrepository: externalRepository): ViewModel() {

    private  var _ticketnumberstart: MutableLiveData<TicketCounterTable> = MutableLiveData()
    val ticketnumberstart: LiveData<TicketCounterTable> = _ticketnumberstart

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
}