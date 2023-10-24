package com.example.erjohnandroid.domain.model.request

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TripCost( var amount: Double,
                     var costType: String,
                     var dateTimeStamp: String,
                     var driverConductorName: String,
                     var line: String
):Parcelable
