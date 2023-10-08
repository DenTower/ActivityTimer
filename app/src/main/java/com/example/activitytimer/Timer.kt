package com.example.activitytimer

data class Timer(
    val currentTime: Long = 0L,
    val isTimerRunning: Boolean = false,
    val itemId: Int? = null
)
