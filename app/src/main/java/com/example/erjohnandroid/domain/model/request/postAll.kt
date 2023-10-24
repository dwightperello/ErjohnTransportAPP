package com.example.erjohnandroid.domain.model.request




data class postAllItem(
    val cancelledTicket: Double,
    val conductorCommission: Double,
    val conductorName: String,
    val dateTimeStamp: String,
    val driverCommission: Double,
    val driverName: String,
    val finalRemit: Double,
    val inFault: String,
    val inspectionreport: List<Inspectionreport>?,
    val manualTicket: Double,
    val mpadassignments: List<Mpadassignment>?,
    val net: Double,
    val partialRemit: Double,
    val partialremitsdetails: List<Partialremitsdetail>?,
    val shororOver: Double,
    val totalCollection: Double,
    val totalExpenses: Double,
    val totalWitholding: Double,
    val tripcost: List<costtrip>?,
    val triptickets: List<tickettrip>?,
    val tripwitholding: List<Tripwitholding>?
)

data class Inspectionreport(
    val actualPassengerCount: Int,
    val dateTimeStamp: String,
    val difference: Int,
    val direction: String,
    val id: Int,
    val ingId: Int,
    val inspectorName: String,
    val line: String,
    val lineSegment: String,
    val mPadUnit: String,
    val qty: Int
)

data class Mpadassignment(
    val busNumber: String,
    val conductorName: String,
    val dataTimeStamp: String,
    val dispatcherName: String,
    val driverName: String,
    val id: Int,
    val ingressoId: Int,
    val line: String,
    val mPadUnit: String
)

data class Partialremitsdetail(
    val amount: Double,
    val amountRemited: Double,
    val cashierName: String,
    val dateTimeStamp: String,
    val id: Int,
    val ingressId: Int,
    val line: String
)



data class Tripwitholding(
    val amount: Double,
    val dateTimeStamp: String,
    val id: Int,
    val ingrId: Int,
    val mPadUnit: String,
    val name: String,
    val witholdingType: String
)