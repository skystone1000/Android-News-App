package com.skystone1000.briefly.domain.usecases.app_entry

import com.skystone1000.briefly.domain.manager.LocalUserManager

class SaveAppEntry(
    private val localUserManager: LocalUserManager
) {
    suspend operator fun invoke() {
        localUserManager.saveAppEntry()
    }
}
