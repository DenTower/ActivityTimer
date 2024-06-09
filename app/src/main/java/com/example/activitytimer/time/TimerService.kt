package com.example.activitytimer.time

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
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
    lateinit var timer: Timer
    private lateinit var item: Entity
    private lateinit var binder: TimerBinder
    override fun onCreate() {
        super.onCreate()
        binder = TimerBinder()
    }

    override fun onBind(p0: Intent?): IBinder {
        Log.d("myLogs", "TimerService: onBind ")
        return binder
    }

    inner class TimerBinder: android.os.Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val itemId = intent.getIntExtra("ItemId", 0)
        val itemName = intent.getStringExtra("ItemName")
        val itemTime = intent.getLongExtra("ItemTime", 0L)
        val itemIsRunning = intent.getBooleanExtra("ItemIsRunning", false)

        item = Entity(itemId, itemName ?: "NameError", itemTime, itemIsRunning)
        timer = Timer(application, item)

        when(intent.action) {
            Actions.START.toString() -> start()
            Actions.STOP.toString() -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }


    private fun start() {
        Log.d("myLogs", "TimerService: start")
        Log.d("myLogs", "TimerService: binder = $binder")


        val openIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val openPendingIntent = PendingIntent.getActivity(
            this,
            0,
            openIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val pauseIntent = Intent(this, TimerPauseReceiver::class.java).apply {
            action = "PAUSE"
        }
        val pausePendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            pauseIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val timerNotification = NotificationCompat.Builder(this, "timer_channel")
            .setSilent(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Activity: ${timer.activity.name}")
            .setContentText("Time: null")
            .setContentIntent(openPendingIntent)
            .addAction(R.drawable.ic_launcher_foreground, "pause", pausePendingIntent)

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

    fun stopTimer() {
        Log.d("myLogs", "TimerService: stopTimer")
        timer.stop().launchIn(CoroutineScope(Dispatchers.Main))
    }

    private fun stop() {
        Log.d("myLogs", "TimerService: stop")
        stopTimer()
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        Log.d("myLogs", "TimerService: destroy")
    }

    enum class Actions {
        START, STOP
    }
}