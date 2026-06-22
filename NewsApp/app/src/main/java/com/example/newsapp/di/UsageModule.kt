package com.example.newsapp.di

import android.app.Application
import com.example.newsapp.data.usage.ApiUsageStoreImpl
import com.example.newsapp.domain.usage.ApiUsageStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UsageModule {

    @Provides
    @Singleton
    fun provideApiUsageStore(application: Application): ApiUsageStore =
        ApiUsageStoreImpl(application)
}
