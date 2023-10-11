package com.example.erjohnandroid.domain.model.response

data class response_login(
    val email: String,
    val fullName: String,
    val token: String,
    val userId: String
)