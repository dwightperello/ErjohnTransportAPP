package com.example.erjohnandroid.database.Model.convertions

import android.util.Log
import com.example.erjohnandroid.database.Model.LineSegmentTable
import com.example.erjohnandroid.database.Model.TripTicketTable
import com.example.erjohnandroid.util.GlobalVariable
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

object TicketConvertions {

    val convertTripTickets :(Double,String,String,String,String,String,String,String,String,Int, Int,Int,Int,Int) -> TripTicketTable ={ amount,conductorname,destination,drivername,line,
                                                                                                                                    mpadunit,origin,passengertype,ticketnumber,reverse,
                                                                                                                                    kmdestination,kmorigin,qty,ingressorefid->

        val formattedDateTime = getCurrentDateInFormat()
        val method= TripTicketTable(
            TripTicketId = 0,
            amount = amount,
            conductorName = conductorname,
            dateTimeStamp = formattedDateTime,
            destination = destination,
            driverName = drivername,
            line= line,
            mPadUnit = mpadunit,
            origin= origin,
            passengerType = passengertype,
            titcketNumber = ticketnumber,
            tripReverse = reverse,
            KmDestination = kmdestination,
            KMOrigin = kmorigin,
            qty = qty,
            ingressoRefId = ingressorefid

        )
      method
    }

    private  fun getCurrentDateInFormat(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }
}