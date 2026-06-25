package com.skystone1000.briefly.presentation.settings.components

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.skystone1000.briefly.domain.usage.UsageSnapshot
import com.skystone1000.briefly.ui.theme.BriefTheme

private const val WARN_THRESHOLD = 0.7f

/**
 * Free-tier usage meter for one provider: `used / limit`, a colour-coded bar, and the reset time.
 * The number is an on-device estimate (see `ApiUsageStore`).
 */
@Composable
fun UsageMeter(snapshot: UsageSnapshot, modifier: Modifier = Modifier) {
    val colors = BriefTheme.colors
    val fraction = if (snapshot.limit > 0) {
        (snapshot.used.toFloat() / snapshot.limit).coerceIn(0f, 1f)
    } else {
        0f
    }
    val barColor = if (fraction >= WARN_THRESHOLD) colors.warn else colors.accent

    Column(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
            Text(
                text = "${snapshot.used} / ${snapshot.limit} used",
                style = MaterialTheme.typography.labelLarge,
                color = colors.text,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "Resets ${DateUtils.getRelativeTimeSpanString(snapshot.resetAt)}",
                style = MaterialTheme.typography.labelSmall,
                color = colors.textTer,
            )
        }
        Spacer(Modifier.height(7.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(colors.border),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(barColor),
            )
        }
        Text(
            text = "On-device estimate",
            style = MaterialTheme.typography.labelSmall,
            color = colors.textTer,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}
