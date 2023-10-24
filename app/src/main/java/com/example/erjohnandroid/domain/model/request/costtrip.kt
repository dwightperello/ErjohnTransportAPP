package com.example.erjohnandroid.domain.model.request

data class costtrip(   val amount: Double,
                       val costType: String,
                       val dateTimeStamp: String,
                       val driverConductorName: String,
                       val id: Int,
                       val ingresId: Int,
                       val line: String)

