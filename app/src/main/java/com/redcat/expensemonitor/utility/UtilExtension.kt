package com.redcat.expensemonitor.utility

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.redcat.expensemonitor.model.Expense
import jxl.Workbook
import jxl.WorkbookSettings
import jxl.write.Label
import jxl.write.WriteException
import jxl.write.biff.RowsExceededException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.io.File
import java.util.*

class UtilExtension {
    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
        private const val wantPermission: String = Manifest.permission.WRITE_EXTERNAL_STORAGE

        fun checkPermission(context: Context): Boolean {
            val checkSelfPermission = ContextCompat.checkSelfPermission(context, wantPermission)
            return checkSelfPermission == PackageManager.PERMISSION_GRANTED
        }

        fun askPermission(activity: Activity) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(wantPermission),
                PERMISSION_REQUEST_CODE
            )
        }

        suspend fun saveExcel(list: List<Expense>, fileName: String, context: Context): Unit {
            var isSuccess = false
            val fileNameExcel = fileName
            //Saving file in external storage
            val sdCard = Environment.getExternalStorageDirectory()
            val directory = File(sdCard.absolutePath + "/expense_monitor")
            if (!directory.exists()) directory.mkdirs()

            val file = File(directory, fileNameExcel)
            val workbookSettings = WorkbookSettings()
            workbookSettings.locale = Locale("en", "EN")
            val launch = CoroutineScope(Dispatchers.IO).async {
                try {
                    val workbook = Workbook.createWorkbook(file, workbookSettings)
                    val sheet = workbook.createSheet(fileName, 0)
                    sheet.addCell(Label(0, 0, "Title"))
                    sheet.addCell(Label(1, 0, "Amount"))
                    sheet.addCell(Label(2, 0, "Category"))
                    sheet.addCell(Label(3, 0, "Date"))
                    var sum = 0.0
                    var i = 0
                    list.forEach { expense ->
                        sheet.addCell(Label(0, i + 1, expense.title))
                        sheet.addCell(Label(1, i + 1, expense.amount.toString()))
                        sheet.addCell(Label(2, i + 1, expense.category))
                        sheet.addCell(
                            Label(3, i + 1, "${expense.date}/${expense.month}/${expense.year}")
                        )
                        sum += expense.amount
                        i++
                    }
                    sheet.addCell(Label(0, i + 1, "Total"))
                    sheet.addCell(Label(1, i + 1, sum.toString()))
                    workbook.write()
                    workbook.close()
                    isSuccess = true
                } catch (e: RowsExceededException) {
                    isSuccess = false
                    e.printStackTrace()
                } catch (e: WriteException) {
                    isSuccess = false
                    e.printStackTrace()
                }
            }
            return launch.await()
        }

        fun Context.showToast(msg: String) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        fun View.showSnackBar(msg: String) {
            val snackBar = Snackbar.make(this, msg, Snackbar.LENGTH_SHORT)

            snackBar.show()
        }
    }

}