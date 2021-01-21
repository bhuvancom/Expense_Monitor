package com.redcat.expensemonitor.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ExpenseWithCatGroup(
    var category: String = "",
    @ColumnInfo(name = "total") var total: Double = 0.0
) : Parcelable