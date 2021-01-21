package com.redcat.expensemonitor.utility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.redcat.expensemonitor.db.ExpenseDao
import com.redcat.expensemonitor.ui.MainViewModel
import com.redcat.expensemonitor.ui.viewmodel.DailyExpenseViewModel

class VMFactory(private val dao: ExpenseDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(dao) as T
            modelClass.isAssignableFrom(DailyExpenseViewModel::class.java) -> DailyExpenseViewModel(
                dao
            ) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class $modelClass")
        }
    }
}