package com.skystone1000.briefly.di

import android.app.Application
import com.skystone1000.briefly.data.manager.LocalUserManagerImpl
import com.skystone1000.briefly.data.manager.SettingsManagerImpl
import com.skystone1000.briefly.domain.manager.LocalUserManager
import com.skystone1000.briefly.domain.manager.SettingsManager
import com.skystone1000.briefly.domain.usecases.app_entry.AppEntryUseCases
import com.skystone1000.briefly.domain.usecases.app_entry.ReadAppEntry
import com.skystone1000.briefly.domain.usecases.app_entry.SaveAppEntry
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Application-scoped bindings. Feature/data modules are added in later phases.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLocalUserManager(application: Application): LocalUserManager =
        LocalUserManagerImpl(application)

    @Provides
    @Singleton
    fun provideSettingsManager(application: Application): SettingsManager =
        SettingsManagerImpl(application)

    @Provides
    @Singleton
    fun provideAppEntryUseCases(localUserManager: LocalUserManager): AppEntryUseCases =
        AppEntryUseCases(
            readAppEntry = ReadAppEntry(localUserManager),
            saveAppEntry = SaveAppEntry(localUserManager)
        )
}
