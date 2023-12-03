package com.example.activitytimer.time

fun formatTime(totalSeconds: Long?): String {
    val hours = totalSeconds?.div(3600L)
    val minutes = (totalSeconds?.rem(3600L))?.div(60L)
    val seconds = totalSeconds?.rem(60L)

    val timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds)

    return timeString
}