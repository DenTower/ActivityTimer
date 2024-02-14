package com.example.activitytimer.time

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log

class TimerPauseReceiver : BroadcastReceiver() {
    var bound = false

    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action == "PAUSE") {
            lateinit var timerService: TimerService
            val sConn = object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                    timerService = (binder as TimerService.TimerBinder).getService()
                    bound = true
                    Log.d("myLogs", "TimerService: connected")
                    timerService.apply {
                        stopTimer()
                        stopSelf()
                    }
                }

                override fun onServiceDisconnected(p0: ComponentName?) {
                    bound = false
                    Log.d("myLogs", "TimerService: disconnected")
                }

            }

            val sIntent = Intent(context, TimerService::class.java)
            context.applicationContext.bindService(sIntent, sConn, 0)

            Log.d("myLogs", "TimerService: bound = $bound")
        }
        Log.d("myLogs", "Конец onReceive")
    }
}