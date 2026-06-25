package com.skystone1000.briefly.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ArticleEntity::class, AiInsightEntity::class, ReadingHistoryEntity::class],
    version = 3
)
abstract class NewsDatabase : RoomDatabase() {
    abstract val newsDao: NewsDao
    abstract val aiInsightDao: AiInsightDao
    abstract val readingHistoryDao: ReadingHistoryDao

    companion object {
        const val NAME = "news.db"
    }
}
