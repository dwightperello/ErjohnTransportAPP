package com.example.erjohnandroid.domain.model.request

data class PartialRemit(
    val amount: Double,
    val amountRemited: Double,
    val cashierName: String,
    val dateTimeStamp: String,
    val line: String
)