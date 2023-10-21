package com.example.activitytimer

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

class TimerService: Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            Action.START.toString() -> start()
            Action.STOP.toString() -> stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val notification = NotificationCompat.Builder(this, "timer_service")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Activity is going")
            .setContentText("name: currentTime")

        startForeground(1, notification.build())
    }

    enum class Action {
        START, STOP
    }
}