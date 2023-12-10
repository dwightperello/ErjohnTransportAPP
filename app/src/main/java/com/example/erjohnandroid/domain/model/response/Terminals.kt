package com.example.erjohnandroid.domain.model.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class TerminalsItem(
    val description: String?,
    val id: Int?,
    val name: String?
):Parcelable