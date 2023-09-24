package com.example.activitytimer.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DataAccessObject {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(entity: Entity)
    @Delete
    suspend fun deleteItem(entity: Entity)
    @Query("SELECT * FROM activities")
    fun getAllItems(): Flow<List<Entity>>
}