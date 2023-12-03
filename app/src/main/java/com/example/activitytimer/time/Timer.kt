package com.example.activitytimer.time

import android.content.Context
import com.example.activitytimer.data.Entity
import com.example.activitytimer.data.MainDb
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class Timer(private val appContext: Context, private val activity: Entity) {

    private val database by lazy { MainDb.getInstance(appContext) }
    val activityName = activity.name
    fun start(interval: Long): Flow<String> {
        return flow {
            var currentTime = activity.time
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
}