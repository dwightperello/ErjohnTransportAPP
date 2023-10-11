package com.example.erjohnandroid.database.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.erjohnandroid.database.Model.TripTicketTable
import com.example.erjohnandroid.database.repository.RoomRepository
import com.example.erjohnandroid.database.repository.sd_Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class sd_viewmodel @Inject constructor(private val repository: sd_Repository): ViewModel() {

    private var _sdtripticket: MutableLiveData<List<TripTicketTable>> = MutableLiveData()
    val sdtripticket: LiveData<List<TripTicketTable>> = _sdtripticket

    private var _sdtripticketdate: MutableLiveData<List<TripTicketTable>> = MutableLiveData()
    val sdtripticketdate: LiveData<List<TripTicketTable>> = _sdtripticketdate

    fun getTripticket(){
        viewModelScope.launch() {
            val records=  repository.getTripTicket()
            _sdtripticket.value=records
        }
    }

    fun insertTripTicketBulk(entity: TripTicketTable){
        viewModelScope.launch() {
            val records=  repository.insertTripticketBulk(entity)

        }
    }

    fun selectTicket_bydate(datetimestamp:String){
        viewModelScope.launch() {
            val records=  repository.selectTicket_bydate(datetimestamp)
            _sdtripticketdate.value=records
        }
    }

}