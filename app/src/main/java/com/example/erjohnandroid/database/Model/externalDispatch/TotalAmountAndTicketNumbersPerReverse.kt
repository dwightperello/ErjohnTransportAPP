package com.example.erjohnandroid.database.Model.externalDispatch

data class TotalAmountAndTicketNumbersPerReverse(
    val tripReverse: Int?,
    val totalAmount: Double?,
    val lastTicketNumber: String?,
    val firstTicketNumber:String,
    val ticketCount:Int
//    val lastId:Int
)
