package com.skystone1000.briefly.di

import android.app.Application
import com.skystone1000.briefly.data.debug.DebugSettingsStoreImpl
import com.skystone1000.briefly.domain.debug.DebugSettingsStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Debug-tooling bindings. Behaviour is gated on `BuildConfig.DEBUG` at the use sites. */
@Module
@InstallIn(SingletonComponent::class)
object DebugModule {

    @Provides
    @Singleton
    fun provideDebugSettingsStore(application: Application): DebugSettingsStore =
        DebugSettingsStoreImpl(application)
}
