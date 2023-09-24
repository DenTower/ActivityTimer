package com.example.activitytimer

import android.app.Application
import com.example.activitytimer.data.MainDb

class App : Application() {
    val database by lazy { MainDb.createDataBase(this) }
}