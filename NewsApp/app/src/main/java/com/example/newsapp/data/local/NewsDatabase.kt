package com.example.newsapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ArticleEntity::class, AiInsightEntity::class], version = 2)
abstract class NewsDatabase : RoomDatabase() {
    abstract val newsDao: NewsDao
    abstract val aiInsightDao: AiInsightDao

    companion object {
        const val NAME = "news.db"
    }
}
