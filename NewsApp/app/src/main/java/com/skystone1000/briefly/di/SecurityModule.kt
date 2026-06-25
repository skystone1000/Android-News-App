package com.skystone1000.briefly.di

import android.app.Application
import com.skystone1000.briefly.data.security.EncryptedApiKeyStore
import com.skystone1000.briefly.domain.security.ApiKeyStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    @Provides
    @Singleton
    fun provideApiKeyStore(application: Application): ApiKeyStore =
        EncryptedApiKeyStore(application)
}
