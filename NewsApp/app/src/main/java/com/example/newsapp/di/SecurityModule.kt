package com.example.newsapp.di

import android.app.Application
import com.example.newsapp.data.security.EncryptedApiKeyStore
import com.example.newsapp.domain.security.ApiKeyStore
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
