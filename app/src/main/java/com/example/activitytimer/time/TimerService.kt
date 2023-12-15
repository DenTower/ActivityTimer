package com.example.activitytimer.time

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.activitytimer.MainActivity
import com.example.activitytimer.R
import com.example.activitytimer.data.Entity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class TimerService: Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var timer: Timer
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val itemId = intent.getIntExtra("ItemId", 0)
        val itemName = intent.getStringExtra("ItemName")
        val itemTime = intent.getLongExtra("ItemTime", 0L)
        val itemIsRunning = intent.getBooleanExtra("ItemIsRunning", false)

        val item = Entity(itemId, itemName!!, itemTime, itemIsRunning)
        timer = Timer(application, item)

        when(intent.action) {
            Actions.START.toString() -> start()
            Actions.STOP.toString() -> stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val openIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val openPendingIntent = PendingIntent.getActivity(
            this,
            0,
            openIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val timerNotification = NotificationCompat.Builder(this, "timer_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Activity: ${timer.activityName}")
            .setContentText("Time: null")
            .setContentIntent(openPendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        timer
            .start(1L)
            .onEach { time ->
                val updatedNotification = timerNotification.setContentText(
                    "Time: $time"
                )
                notificationManager.notify(1, updatedNotification.build())
            }
            .launchIn(serviceScope)

        startForeground(1, timerNotification.build())
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    enum class Actions {
        START, STOP
    }
}