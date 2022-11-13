package com.redcat.expensemonitor.utility

import java.util.*

class Constants {
    companion object {
        val CAT_LIST =
            listOf(
                "Food",
                "Grocery",
                "Shopping",
                "Transportation",
                "Bills",
                "Medical & Health Care",
                "Entertainment",
                "Investment & Saving",
                "Extra"
            )
        const val DB_NAME = "expense.db"
        const val EXPENSE_TABLE = "expense"
        val MONTHS = listOf(
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December"
        )

        fun tillYear(): MutableList<Int> {
            val years = mutableListOf<Int>()
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            for (i in 2022..(currentYear)) years.add(i)
            return years
        }

        fun getTodayDate(): Int {
            return Calendar.getInstance(TimeZone.getDefault()).get(Calendar.DAY_OF_MONTH)
        }

        fun getCurrentMonth(): String {
            val monthVal = Calendar.getInstance(TimeZone.getDefault()).get(Calendar.MONTH)
            return MONTHS[monthVal]
        }

        fun getCurrentYear(): Int = Calendar.getInstance(TimeZone.getDefault()).get(Calendar.YEAR)
    }
}