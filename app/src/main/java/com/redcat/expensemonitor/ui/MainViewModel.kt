package com.redcat.expensemonitor.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.sqlite.db.SimpleSQLiteQuery
import com.redcat.expensemonitor.db.ExpenseDao
import com.redcat.expensemonitor.model.Expense
import com.redcat.expensemonitor.model.ExpenseWithCatGroup
import kotlinx.coroutines.launch

class MainViewModel(
    private val dao: ExpenseDao
) :
    ViewModel() {

    val shouldHideExportAndGraph = MutableLiveData<Boolean>()

    fun updateHide(isHide: Boolean) {
        shouldHideExportAndGraph.postValue(isHide)
    }

    private val pagerConfig = PagingConfig(
        pageSize = 60,
        enablePlaceholders = true,
        maxSize = 1000
    )
    private val TAG = "VM_EXPENSE"
    fun saveExpense(expense: Expense) = viewModelScope.launch {
        Log.d(TAG, "saveExpense: saving $expense ")
        dao.upsert(expense)
    }

    fun deleteExpense(expense: Expense) = viewModelScope.launch {
        Log.d(TAG, "deleteExpense: deleting $expense ")
        dao.deleteExpense(expense)
    }

    fun getAllExpenses() = Pager(pagerConfig) { dao.getAllExpense() }.flow
    fun getTotal() = dao.getTotalAmount()

    suspend fun getExpenseListOfDate(date: Int, month: String, year: Int) =
        dao.getExpenseListOfGivenDate(date, month, year)


    suspend fun getExpenseListOfGivenMonthYear(month: String, year: Int) =
        dao.getExpenseListOFGivenMonthAndYear(month, year)


    suspend fun getExpenseListOfGivenYear(year: Int) =
        dao.getExpenseListOfYear(year)

    suspend fun getAllExpenseList() = dao.getAllListExpense()


    fun totalForToday(date: Int, month: String, year: Int) = dao.getTotalOfTheDay(date, month, year)
    fun getExpenseOfDate(date: Int, month: String, year: Int) = Pager(pagerConfig) {
        dao.getExpenseOfGivenDate(date, month, year)
    }.flow

    fun getExpensesOfMonthYear(month: String, year: Int) = Pager(pagerConfig) {
        dao.getExpenseOFGivenMonthAndYear(month, year)
    }.flow

    fun getTotalOfGivenMonthAndYear(month: String, year: Int) =
        dao.getTotalOFGivenMonthAndYear(month, year)


    fun getExpenseOfYear(year: Int) = Pager(pagerConfig) { dao.getExpenseOfGivenYear(year) }.flow
    fun getTotalOfYear(year: Int) = dao.getAmountOfGivenYear(year)

    suspend fun getCatDMY(date: Int, month: String, year: Int): List<ExpenseWithCatGroup> {
        val query =
            "select category,sum(amount) as total from expense where date=?" + "and month=?" + "and year=?" + "group by category"
        val args = listOf(date, month, year)
        val sqlLite = SimpleSQLiteQuery(query, args.toTypedArray())
        return dao.getCatTotal(sqlLite)
    }

    suspend fun getCATMY(month: String, year: Int): List<ExpenseWithCatGroup> {
        val query =
            "select category,sum(amount) as total from expense where month=? and year=? group by category"
        val args = listOf(month, year)
        val sqlLite = SimpleSQLiteQuery(query, args.toTypedArray())
        return dao.getCatTotal(sqlLite)
    }

    suspend fun getCatY(year: Int): List<ExpenseWithCatGroup> {
        val query =
            "select category,sum(amount) as total from expense where year=? group by category"
        val args = listOf(year)
        val sqlLite = SimpleSQLiteQuery(query, args.toTypedArray())
        return dao.getCatTotal(sqlLite)
    }

    suspend fun getCAT(): List<ExpenseWithCatGroup> {
        val query =
            "select category,sum(amount) as total from expense  group by category"
        val args = emptyArray<String>()
        val sqlLite = SimpleSQLiteQuery(query, args)
        return dao.getCatTotal(sqlLite)
    }
}