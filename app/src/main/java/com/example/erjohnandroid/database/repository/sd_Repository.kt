package com.example.erjohnandroid.database.repository

import com.example.erjohnandroid.database.Model.TripTicketTable
import com.example.erjohnandroid.database.sdcard_dao.sd_TripticketDao
import javax.inject.Inject

class sd_Repository @Inject constructor(private val sdTripticketdao: sd_TripticketDao) {

    fun getTripTicket():List<TripTicketTable>{
        return  sdTripticketdao.getTriptikcet()
    }

    fun insertTripticketBulk(entity: TripTicketTable){
        return sdTripticketdao.inserTripticketBulk(entity)
    }

    suspend fun selectTicket_bydate(datetimestamp:String):List<TripTicketTable>{
        return sdTripticketdao.selectTicket_bydate(datetimestamp)
    }
}