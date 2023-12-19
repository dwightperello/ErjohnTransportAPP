package com.example.erjohnandroid.util

import com.example.erjohnandroid.database.Model.*
import com.paymaya.sdk.android.checkout.models.Item
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

object GlobalVariable {
   //const val API_BASE_URL="http://10.0.0.9:8084/api/"
   //const val API_BASE_URL="http://192.168.0.100:8084/api/"
    // const val API_BASE_URL="http://192.168.1.70:8085/api/"
   const val API_BASE_URL="http://192.168.0.102:8085/api/"
   //const val API_BASE_URL="http://bfh6183-001-site1.ctempurl.com/api/"

    var token:String?= null
    //const val API_BASE_URL="http://a2zsolutions-001-site1.etempurl.com/api/"

    var employeeName:String?= null

    var inspectorname:String?= null
    var terminal:String?= null


    var ReverseTotalAmount:Double=0.0

    var cashiername:String?= null

    var line:String?=null
    var lineid:Int?=null

    var direction:String?= null
    var conductor:String?= null
    var driver:String?= null
    var bus:String?= null

    var isDispatched:Boolean=false

    var checkedPosition:Int?= null

    var tripreverse:Int?= 1

    var ticketnumber:Int=0
    var originalTicketnum:Int=0

    var batLevel:String?= null

    var deviceName:String?= null
    var machineName:String?= "0000000"
    var permitNumber:String?= "0000000"
    var serialNumber:String?= "0000000"
    var linesegment:List<LineSegmentTable>?= arrayListOf()

    var hotspot:List<HotSpotsTable>?= arrayListOf()

    var remainingPass:Int?=null

    var ticketcounter:Int=0

    var destinationcounter:Int=1
    var origincounter:Int=0

    var AllWitholding:ArrayList<TripWitholdingTable> = arrayListOf()
    var AllTripCost:ArrayList<TripCostTable> = arrayListOf()
    var bonusArraylist:ArrayList<TripCostTable> = arrayListOf()

    var ticketnumid:Int?= 1
    var ingressoRefId:Int=0

    var isFromDispatch:Boolean=false

    var priorWitholdingAmount:Double=0.0

    var witholds: java.util.ArrayList<TripWitholdingTable> = arrayListOf()
    var expenses:ArrayList<TripCostTable> = arrayListOf()


    var discountAmount:Double=0.0
    var basefair:Double=0.0
    var exceedAmount:Double=0.0
    var specialexceedAmount=0.0

    var arrayLogReport:ArrayList<LogReport> = arrayListOf()


 val saveLogreport:(String) ->Unit ={message->
        val formattedDateTime = getCurrentDateInFormat()

        var logreport = LogReport(
         LogReportId=0,
         dateTimeStamp= formattedDateTime,
         deviceName=GlobalVariable.deviceName!!,
         description = "${message}",
         ingressoRefId = GlobalVariable.ingressoRefId
        )

       GlobalVariable.arrayLogReport.add(logreport)

    }

 val saveLogreportlogin:(String) ->Unit ={message->
  val formattedDateTime = getCurrentDateInFormat()

  var logreport = LogReport(
   LogReportId=0,
   dateTimeStamp= formattedDateTime,
   deviceName="no device name yet, retrieving",
   description = "${message}",
   ingressoRefId = 0
  )

  GlobalVariable.arrayLogReport.add(logreport)

 }

   private fun getCurrentDateInFormat(): String {
     val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
     val currentDate = Date()
     return dateFormat.format(currentDate)
    }

}