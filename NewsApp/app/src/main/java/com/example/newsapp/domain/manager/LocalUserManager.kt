package com.example.newsapp.domain.manager

import kotlinx.coroutines.flow.Flow

/**
 * Persists small bits of local user state (e.g. whether onboarding is done).
 * Pure-Kotlin contract; the implementation lives in the data layer.
 */
interface LocalUserManager {

    suspend fun saveAppEntry()

    fun readAppEntry(): Flow<Boolean>
}
