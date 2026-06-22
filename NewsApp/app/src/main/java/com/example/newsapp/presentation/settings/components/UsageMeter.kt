package com.example.newsapp.presentation.settings.components

import android.text.format.DateUtils
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.newsapp.domain.usage.UsageSnapshot

private const val AMBER_THRESHOLD = 0.7f
private const val RED_THRESHOLD = 0.9f

/**
 * Free-tier usage meter for one provider: `used / limit`, a colour-coded bar, and the reset time.
 * The number is an on-device estimate (see `ApiUsageStore`).
 */
@Composable
fun UsageMeter(snapshot: UsageSnapshot, modifier: Modifier = Modifier) {
    val fraction = if (snapshot.limit > 0) {
        (snapshot.used.toFloat() / snapshot.limit).coerceIn(0f, 1f)
    } else {
        0f
    }
    val barColor = when {
        fraction >= RED_THRESHOLD -> MaterialTheme.colorScheme.error
        fraction >= AMBER_THRESHOLD -> Color(0xFFFFA000)
        else -> MaterialTheme.colorScheme.primary
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "${snapshot.used} / ${snapshot.limit} used",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Resets ${DateUtils.getRelativeTimeSpanString(snapshot.resetAt)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            color = barColor
        )
        if (fraction >= RED_THRESHOLD) {
            Text(
                text = "Near the free-tier limit — calls may start failing.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Text(
            text = "On-device estimate",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
