package com.redcat.expensemonitor.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.redcat.expensemonitor.model.Expense
import com.redcat.expensemonitor.utility.Constants

@Database(entities = [Expense::class], version = 1)
abstract class ExpenseMonitorDatabase : RoomDatabase() {

    abstract fun getExpenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var instance: ExpenseMonitorDatabase? = null

        fun getDatabase(context: Context): ExpenseMonitorDatabase {
            val tempInstance = instance
            if (tempInstance != null) return tempInstance
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    ExpenseMonitorDatabase::class.java,
                    Constants.DB_NAME
                ).build()
                this.instance = instance
                return this.instance!!
            }
        }
    }
}