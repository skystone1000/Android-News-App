package com.skystone1000.briefly.presentation.debug

import androidx.lifecycle.ViewModel
import com.skystone1000.briefly.data.debug.RequestEntry
import com.skystone1000.briefly.data.debug.RequestLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class RequestLogViewModel @Inject constructor(
    private val requestLog: RequestLog,
) : ViewModel() {

    val entries: StateFlow<List<RequestEntry>> = requestLog.entries

    fun clear() = requestLog.clear()
}
