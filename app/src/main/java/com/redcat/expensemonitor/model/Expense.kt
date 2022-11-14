package com.redcat.expensemonitor.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.redcat.expensemonitor.utility.Constants
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Entity(tableName = Constants.EXPENSE_TABLE)
@Parcelize
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val title: String = "",
    val amount: Double = 0.00,
    val date: Int = 1,
    val month: String = "January",
    val year: Int = 2021,
    val category: String = ""
) : Serializable, Parcelable