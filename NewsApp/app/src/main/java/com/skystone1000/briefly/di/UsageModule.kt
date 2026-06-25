package com.skystone1000.briefly.di

import android.app.Application
import com.skystone1000.briefly.data.usage.ApiUsageStoreImpl
import com.skystone1000.briefly.domain.usage.ApiUsageStore
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
