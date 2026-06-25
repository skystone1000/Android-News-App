package com.skystone1000.briefly.domain.usecases.app_entry

import com.skystone1000.briefly.domain.manager.LocalUserManager
import kotlinx.coroutines.flow.Flow

class ReadAppEntry(
    private val localUserManager: LocalUserManager
) {
    operator fun invoke(): Flow<Boolean> = localUserManager.readAppEntry()
}
