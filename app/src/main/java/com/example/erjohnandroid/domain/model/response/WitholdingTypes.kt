package com.example.erjohnandroid.domain.model.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize



@Parcelize
data class WitholdingTypesItem(
    val id: Int,
    val type: String
):Parcelable