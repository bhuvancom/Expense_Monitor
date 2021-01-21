package com.redcat.expensemonitor.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.redcat.expensemonitor.db.ExpenseDao

class DailyExpenseViewModel(private val dao: ExpenseDao) : ViewModel() {
    
    fun totalForToday(date: Int, month: String, year: Int) = dao.getTotalOfTheDay(date, month, year)

    fun allExpense(date: Int, month: String, year: Int) = Pager(
        PagingConfig(
            pageSize = 60,
            enablePlaceholders = true,
            maxSize = 200
        )
    ) {
        dao.getExpenseOfGivenDate(date, month, year)
    }.flow
}