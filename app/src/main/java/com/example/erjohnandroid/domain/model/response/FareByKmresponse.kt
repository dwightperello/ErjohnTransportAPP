package com.example.erjohnandroid.domain.model.response



data class FareByKmItem(
    val amount: Int,
    val discountrate: Int,
    val id: Int,
    val lineid: Int,
    val lowekmlimit: Int,
    val totalkm: Int,
    val upperkmlimit: Int
)