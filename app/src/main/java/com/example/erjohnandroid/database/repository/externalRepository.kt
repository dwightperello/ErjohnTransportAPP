package com.example.erjohnandroid.database.repository

import com.example.erjohnandroid.database.Model.TicketCounterTable
import com.example.erjohnandroid.database.dao.ExternalDBDao
import com.example.erjohnandroid.database.dao.TicketNumDAO
import javax.inject.Inject

class externalRepository  @Inject constructor(private val externalticketnum: ExternalDBDao) {

    fun getTicketnumbers(): TicketCounterTable {
        return externalticketnum.getTicketstart()
    }

    fun updateTicketnumbers(ticketcounter:Int,refid: Int, id: Int){
        return externalticketnum.updateTicketstart(ticketcounter,refid, id)
    }
    fun insertticketnum(entity: TicketCounterTable){
        return externalticketnum.insertticketnum(entity)
    }
}