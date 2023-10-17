package com.example.erjohnandroid.domain.model.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize



@Parcelize
data class CompanyRolesItem(
    val employee: List<Employee>,
    val id: Int,
    val name: String,
    val tag: Int
):Parcelable

@Parcelize
data class Employee(
    val companyRolesId: Int,
    val id: Int,
    val lastName: String?,
    val name: String,
    val pin: Int
):Parcelable