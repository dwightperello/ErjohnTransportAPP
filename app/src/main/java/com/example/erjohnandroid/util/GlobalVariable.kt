package com.example.erjohnandroid.util

import com.example.erjohnandroid.database.Model.HotSpotsTable
import com.example.erjohnandroid.database.Model.LineSegmentTable
import com.example.erjohnandroid.database.Model.TripCostTable
import com.example.erjohnandroid.database.Model.TripWitholdingTable
import com.paymaya.sdk.android.checkout.models.Item

object GlobalVariable {
   const val API_BASE_URL="http://10.0.0.9:8084/api/"
  // const val API_BASE_URL="http://192.168.0.100:8084/api/"
   //const val API_BASE_URL="http://bfh6183-001-site1.ctempurl.com/api/"

    var token:String?= null
    //const val API_BASE_URL="http://a2zsolutions-001-site1.etempurl.com/api/"

    var employeeName:String?= null

    var inspectorname:String?= null

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




}