package com.example.erjohnandroid.database.Model.convertions

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.erjohnandroid.database.Model.LineSegmentTable
import com.example.erjohnandroid.database.Model.TripTicketTable
import com.example.erjohnandroid.util.GlobalVariable
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

object TicketConvertions {

    @RequiresApi(Build.VERSION_CODES.O)
    val convertTripTickets :(Double, String, String, String, String, String, String, String, String, Int, Int, Int, Int, Int) -> TripTicketTable ={ amount, conductorname, destination, drivername, line,
                                                                                                                                                    mpadunit, origin, passengertype, ticketnumber, reverse,
                                                                                                                                                    kmdestination, kmorigin, qty, ingressorefid->
        val currentTime: LocalTime = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm") // Format to get only hours and minutes
        val formattedTime: String = currentTime.format(formatter)
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
            ingressoRefId = ingressorefid,
            time = "${formattedTime}"

        )
      method
    }

    private  fun getCurrentDateInFormat(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }
}