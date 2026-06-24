package com.example.newsapp.presentation.debug

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newsapp.R
import com.example.newsapp.data.debug.RequestEntry
import com.example.newsapp.data.debug.RequestKind
import com.example.newsapp.presentation.common.BriefScreenTitle
import com.example.newsapp.presentation.common.EmptyScreen
import com.example.newsapp.ui.theme.BriefTheme

@Composable
fun RequestLogScreen(
    entries: List<RequestEntry>,
    onClear: () -> Unit,
    navigateUp: () -> Unit,
) {
    val colors = BriefTheme.colors
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg),
    ) {
        Row(
            modifier = Modifier.padding(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = navigateUp) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back_arrow),
                    contentDescription = "Back",
                    tint = colors.text,
                )
            }
            Spacer(Modifier.size(4.dp))
            BriefScreenTitle("Request log")
            Spacer(Modifier.weight(1f))
            if (entries.isNotEmpty()) {
                TextButton(onClick = onClear) { Text("Clear", color = colors.accent) }
            }
        }

        if (entries.isEmpty()) {
            EmptyScreen()
        } else {
            LazyColumn(Modifier.fillMaxSize()) {
                items(count = entries.size) { index ->
                    if (index > 0) HorizontalDivider(thickness = 1.dp, color = colors.divider)
                    RequestRow(entries[index])
                }
            }
        }
    }
}

@Composable
private fun RequestRow(entry: RequestEntry) {
    val colors = BriefTheme.colors
    val kindColor = when (entry.kind) {
        RequestKind.LIVE -> colors.textSec
        RequestKind.SAVED -> colors.accent
        RequestKind.REPLAYED -> colors.good
        RequestKind.ERROR -> colors.warn
    }
    Column(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(entry.kind.name, style = MaterialTheme.typography.labelSmall, color = kindColor)
            Spacer(Modifier.size(8.dp))
            Text(
                "${entry.status ?: "—"} · ${entry.durationMs}ms",
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 11.sp),
                color = colors.textTer,
            )
        }
        Spacer(Modifier.size(4.dp))
        Text(
            "${entry.method} ${entry.url}",
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 11.sp),
            color = colors.text,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
