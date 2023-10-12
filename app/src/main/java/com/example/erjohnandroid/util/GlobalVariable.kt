package com.example.erjohnandroid.util

import com.example.erjohnandroid.database.Model.LineSegmentTable
import com.paymaya.sdk.android.checkout.models.Item

object GlobalVariable {
    const val API_BASE_URL="http://10.0.0.9:8084/api/"

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

    var deviceName:String?= null

    var linesegment:List<LineSegmentTable>?= arrayListOf()

    var remainingPass:Int?=null

    var ticketcounter:Int=1

    var destinationcounter:Int=1
    var origincounter:Int=0




}