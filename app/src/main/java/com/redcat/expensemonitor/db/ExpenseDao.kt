package com.redcat.expensemonitor.db

import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.redcat.expensemonitor.model.Expense
import com.redcat.expensemonitor.model.ExpenseWithCatGroup
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(expense: Expense): Long

    @Query("SELECT * FROM expense order by id desc")
    fun getAllExpense(): PagingSource<Int, Expense>

    @Query("SELECT * FROM expense order by id desc")
    suspend fun getAllListExpense(): List<Expense>

    @Query("SELECT * FROM expense where date = :date and month = :month and year = :year order by date")
    fun getExpenseOfGivenDate(date: Int, month: String, year: Int): PagingSource<Int, Expense>

    @Query("SELECT * FROM expense where date = :date and month = :month and year = :year order by date")
    suspend fun getExpenseListOfGivenDate(date: Int, month: String, year: Int): List<Expense>

    @Query("SELECT * FROM expense where month = :month and year = :year order by id desc")
    fun getExpenseOFGivenMonthAndYear(month: String, year: Int): PagingSource<Int, Expense>

    @Query("SELECT * FROM expense where month = :month and year = :year order by id desc")
    suspend fun getExpenseListOFGivenMonthAndYear(month: String, year: Int): List<Expense>

    @Query("SELECT * FROM expense where year = :year order by id desc")
    fun getExpenseOfGivenYear(year: Int): PagingSource<Int, Expense>

    @Query("SELECT * FROM expense where year = :year order by id desc")
    suspend fun getExpenseListOfYear(year: Int): List<Expense>

    @Query("select sum(amount) from expense")
    fun getTotalAmount(): Flow<Double?>

    @RawQuery
    suspend fun getCatTotal(query: SupportSQLiteQuery): List<ExpenseWithCatGroup>

    @Query("SELECT sum(amount) from expense where date=:date and month=:month and year=:year")
    fun getTotalOfTheDay(date: Int, month: String, year: Int): Flow<Double?>

    @Query("SELECT sum(amount) FROM expense where month = :month and year = :year")
    fun getTotalOFGivenMonthAndYear(month: String, year: Int): Flow<Double?>

    @Query("SELECT sum(amount) FROM expense where year = :year order by id desc")
    fun getAmountOfGivenYear(year: Int): Flow<Double?>


    @Delete
    suspend fun deleteExpense(expense: Expense)
}