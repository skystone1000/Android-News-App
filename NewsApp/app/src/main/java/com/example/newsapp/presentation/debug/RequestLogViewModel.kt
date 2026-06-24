package com.example.newsapp.presentation.debug

import androidx.lifecycle.ViewModel
import com.example.newsapp.data.debug.RequestEntry
import com.example.newsapp.data.debug.RequestLog
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
