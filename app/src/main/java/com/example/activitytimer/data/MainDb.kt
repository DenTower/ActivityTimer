package com.example.activitytimer.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        Entity::class
    ],
    version = 1
)
abstract class MainDb : RoomDatabase() {
    abstract val dao: DataAccessObject

    companion object {
        private var INSTANCE: MainDb? = null
        fun getInstance(context: Context): MainDb {
            if(INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context,
                    MainDb::class.java,
                    "activity.db"
                ).build()
            }
            return INSTANCE as MainDb

        }
    }
}