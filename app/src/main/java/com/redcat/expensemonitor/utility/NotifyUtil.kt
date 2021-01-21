package com.redcat.expensemonitor.utility

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.redcat.expensemonitor.R
import java.io.File


object NotifyUtil {

    private const val CHANNEL_ID = "EXPENSE MONITOR"
    private const val NOTIFICATION_CHANNEL = "EXPENSE MONITOR NOTIFICATION"

    fun init(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                activity.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
            val existingChannel = notificationManager.getNotificationChannel(CHANNEL_ID)
            if (existingChannel == null) {
                // Create the NotificationChannel
                val name = activity.getString(R.string.defaultChannel)
                val importance = NotificationManager.IMPORTANCE_HIGH
                val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
                //mChannel.description = activity.getString(R.string.notificationDescription)
                notificationManager.createNotificationChannel(mChannel)
            }
        }
    }

    fun notify(context: Context, file: String) {
        val sdCard = Environment.getExternalStorageDirectory()
        val directory = File(sdCard.absolutePath + "/expense_monitor")
        val fileWithPath = File(directory, file)
        val path =
            if (Build.VERSION.SDK_INT < 24) Uri.fromFile(fileWithPath) else Uri.parse(fileWithPath.path)

        val nIntent = Intent(Intent.ACTION_VIEW)
        nIntent.setDataAndType(path, "application/vnd.ms-excel")
        nIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        nIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        val pendingIntent = PendingIntent.getActivity(context, 0, nIntent, 0)
        val notificationBuild = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Expense Monitor")
            .setContentText("File generated at path ${fileWithPath.path}")
            .setChannelId(CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle())
            .setContentIntent(pendingIntent)
        //notificationBuild.priority = NotificationCompat.PRIORITY_HIGH

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1, notificationBuild.build())
    }

    private const val TAG = "NotifyUtil"
}