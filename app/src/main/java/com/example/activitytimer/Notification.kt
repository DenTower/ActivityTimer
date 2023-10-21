package com.example.activitytimer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

class Notification(context: Context, title: String, msg: String) {
    val CHANNEL_ID = "TimeActiveNotify1"
    val CHANNEL_NAME = "TimeActiveNotify"
    val CHANNEL_DESC = "Showing current time of activity"
    var builder = NotificationCompat.Builder(context, CHANNEL_ID )
        .setContentTitle(title)
        .setContentText(msg)
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setOnlyAlertOnce(true)
        .setOngoing(true)
    val notificationManager: NotificationManager =
        context.applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_NAME
            val descriptionText = CHANNEL_DESC
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun fireNotify() {
        createNotificationChannel()
        notificationManager.notify(1, builder.build())
    }
}