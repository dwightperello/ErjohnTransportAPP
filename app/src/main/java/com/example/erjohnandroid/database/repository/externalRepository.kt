package com.example.erjohnandroid.database.repository

import androidx.room.Query
import com.example.erjohnandroid.database.Model.TicketCounterTable
import com.example.erjohnandroid.database.Model.externalDispatch.SavedDispatchInfo
import com.example.erjohnandroid.database.dao.ExternalDBDao
import com.example.erjohnandroid.database.dao.SavedDispatchedDao
import com.example.erjohnandroid.database.dao.TicketNumDAO
import javax.inject.Inject

class externalRepository  @Inject constructor(private val externalticketnum: ExternalDBDao, private  val savedDispatchedDao: SavedDispatchedDao) {

    fun getTicketnumbers(): TicketCounterTable {
        return externalticketnum.getTicketstart()
    }

    fun updateTicketnumbers(ticketcounter:Int,refid: Int, id: Int){
        return externalticketnum.updateTicketstart(ticketcounter,refid, id)
    }
    fun insertticketnum(entity: TicketCounterTable){
        return externalticketnum.insertticketnum(entity)
    }

    fun getSavedDispatch(): SavedDispatchInfo {
        return savedDispatchedDao.checkIfalreadyDispatch()
    }

    fun updateSavedDispatched(busnumber: String,conductorName: String,isDispatched: Boolean,dispatcherName: String,driverName: String,line: String,lineId:Int,mPadUnit: String,reverse:Int, orginalTicketnumber:Int,direction:String,ingressoRefId:Int,machinename:String,permitnumber:String,serialnumber:String){
        return savedDispatchedDao.updateTSavedDispatch(busnumber,conductorName,isDispatched,dispatcherName,driverName,line,lineId,mPadUnit,reverse,orginalTicketnumber,direction,ingressoRefId,machinename,permitnumber,serialnumber)
    }

    fun updateSavedDispatchedReverse(direction:String , reverse: Int){
        return savedDispatchedDao.updateTSavedDispatchdirection(direction,reverse)
    }

    fun updateIsDispatched(isDispatched: Boolean ){
        return savedDispatchedDao.updateIsDispatched(isDispatched)
    }

    fun updateReverseOnly(reverse:Int){
        return savedDispatchedDao.updatereverse(reverse)
    }
    fun insertSavedDispatch(entity: SavedDispatchInfo){
        return savedDispatchedDao.insertSaveDispatch(entity)
    }


}
