package com.example.activitytimer.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activities")
data class Entity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String
)
