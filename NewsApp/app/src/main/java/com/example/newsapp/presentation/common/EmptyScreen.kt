package com.example.newsapp.presentation.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import com.example.newsapp.R
import com.example.newsapp.data.remote.source.MissingApiKeyException

/**
 * Full-screen empty/error state. Shows a friendly message derived from [error]
 * (or a generic "no results" message when [error] is null).
 */
@Composable
fun EmptyScreen(error: LoadState.Error? = null) {
    val message = when (val throwable = error?.error) {
        is MissingApiKeyException ->
            "No API key for this source. Add one in Settings → Data sources."
        is java.net.SocketTimeoutException -> "Server unavailable. Please try again."
        is java.net.ConnectException -> "No internet connection."
        null -> "No articles found."
        else -> throwable.message ?: "Something went wrong."
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_network_error),
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = message,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
