package com.example.erjohnandroid.domain.model.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class LinesItem(
    val id: Int,
    val lineSegments: List<LineSegment>,
    val name: String,
    val remarks: String,
    val tag: Int
):Parcelable

@Parcelize
data class LineSegment(
    val id: Int,
    val kmPoint: Int,
    val lineId: Int,
    val name: String,
    val remarks: String,
    val tag: Int
):Parcelable