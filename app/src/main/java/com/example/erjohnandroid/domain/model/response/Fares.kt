package com.example.erjohnandroid.domain.model.response

data class Fares(
    val baseAmount: Double,
    val discountAmount: Double,
    val exceedAmount: Double,
    val id: Int,
    val name: String,
    val specialExceedAmount: Double
)