package com.skystone1000.briefly.presentation.details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.skystone1000.briefly.presentation.details.AiInsightState
import com.skystone1000.briefly.ui.theme.BriefTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiInsightCard(state: AiInsightState, modifier: Modifier = Modifier) {
    if (state is AiInsightState.Idle) return
    val colors = BriefTheme.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(colors.accentSoft)
            .border(1.dp, colors.accentLine, RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(colors.accent),
            contentAlignment = Alignment.Center,
        ) {
            Text("AI", style = MaterialTheme.typography.labelSmall, color = colors.onAccent)
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Text("AI SUMMARY", style = MaterialTheme.typography.labelSmall, color = colors.accent)
            Spacer(Modifier.size(4.dp))
            when (state) {
                is AiInsightState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = colors.accent,
                    strokeWidth = 2.dp,
                )

                is AiInsightState.Error -> Text(
                    text = state.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.warn,
                )

                is AiInsightState.Success -> {
                    Text(
                        text = state.insight.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSec,
                    )
                    Text(
                        text = "Sentiment: ${state.insight.sentiment}",
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.textTer,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                    if (state.insight.tags.isNotEmpty()) {
                        FlowRow(
                            modifier = Modifier.padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            state.insight.tags.forEach { tag ->
                                Text(
                                    text = tag,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.accent,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(colors.card)
                                        .padding(horizontal = 9.dp, vertical = 4.dp),
                                )
                            }
                        }
                    }
                }

                AiInsightState.Idle -> Unit
            }
        }
    }
}
