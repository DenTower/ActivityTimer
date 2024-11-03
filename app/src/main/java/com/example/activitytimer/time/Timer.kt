package com.example.activitytimer.time

import android.content.Context
import android.util.Log
import com.example.activitytimer.data.Entity
import com.example.activitytimer.data.MainDb
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class Timer(private val appContext: Context, val activity: Entity) {

    private val database by lazy { MainDb.getInstance(appContext) }
    private var currentTime = activity.time
    fun start(interval: Long): Flow<String> {
        return flow {
            while(true) {
                delay(interval * 1000L)
                currentTime += interval
                val item = activity.copy(
                    time = currentTime
                )
                database.dao.insertItem(item)
                emit(formatTime(currentTime))
            }
        }
    }

    fun stop(): Flow<Boolean> {
        return flow {
            val changedItem = activity.copy(
                isRunning = !activity.isRunning,
                time = currentTime
            )
            Log.d("myLogs", "TimerService: timer.stop() $changedItem")
            database.dao.insertItem(changedItem)
            emit(true)
        }
    }
}