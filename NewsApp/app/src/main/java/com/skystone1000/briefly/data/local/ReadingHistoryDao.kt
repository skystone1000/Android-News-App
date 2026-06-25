package com.skystone1000.briefly.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingHistoryDao {

    @Upsert
    suspend fun upsert(entry: ReadingHistoryEntity)

    @Query("SELECT * FROM reading_history ORDER BY readAt DESC")
    fun getHistory(): Flow<List<ReadingHistoryEntity>>

    @Query("DELETE FROM reading_history")
    suspend fun clear()
}
