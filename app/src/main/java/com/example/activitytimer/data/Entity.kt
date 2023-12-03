package com.example.activitytimer.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activities")
data class Entity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String,
    val time: Long = 0L,
    @ColumnInfo(defaultValue = "false")
    val isRunning: Boolean
)
