package com.skystone1000.briefly.presentation.debug

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skystone1000.briefly.R
import com.skystone1000.briefly.domain.debug.DebugConfig
import com.skystone1000.briefly.presentation.common.BriefScreenTitle
import com.skystone1000.briefly.presentation.common.BriefToggle
import com.skystone1000.briefly.presentation.common.SectionHeader
import com.skystone1000.briefly.ui.theme.BriefTheme

@Composable
fun DebugScreen(
    config: DebugConfig,
    sideEffect: String?,
    onToggleSave: (Boolean) -> Unit,
    onToggleOffline: (Boolean) -> Unit,
    onToggleForceError: (Boolean) -> Unit,
    onCycleLatency: () -> Unit,
    onClearMocks: () -> Unit,
    onResetState: () -> Unit,
    onSideEffectShown: () -> Unit,
    navigateToRequestLog: () -> Unit,
    navigateUp: () -> Unit,
) {
    val colors = BriefTheme.colors
    val context = LocalContext.current
    var showResetConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(sideEffect) {
        sideEffect?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            onSideEffectShown()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            modifier = Modifier.padding(start = 14.dp, end = 18.dp, top = 8.dp, bottom = 8.dp),
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
            BriefScreenTitle("Developer options")
        }

        DebugSection("MOCK & OFFLINE") {
            DebugToggleRow("Save API responses", "Capture every call into the device mock folder",
                config.saveResponses, onToggleSave)
            Divider()
            DebugToggleRow("Offline mode", "Replay saved mocks instead of the network",
                config.offlineMode, onToggleOffline)
        }

        DebugSection("SIMULATION") {
            DebugToggleRow("Force API error", "Make every provider call fail",
                config.forceError, onToggleForceError)
            Divider()
            DebugValueRow(
                title = "Network latency",
                value = if (config.latencyMs == 0L) "Off" else "${config.latencyMs} ms",
                onClick = onCycleLatency,
            )
        }

        DebugSection("INSPECT & RESET") {
            DebugValueRow("Request log", "View", navigateToRequestLog)
            Divider()
            DebugActionRow("Clear captured mocks", colors.text, onClearMocks)
            Divider()
            DebugActionRow("Reset app state", colors.warn) { showResetConfirm = true }
        }
        Spacer(Modifier.size(20.dp))
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("Reset app state?") },
            text = { Text("Clears bookmarks, history, AI cache, API keys, and all settings.") },
            confirmButton = {
                TextButton(onClick = {
                    showResetConfirm = false
                    onResetState()
                }) { Text("Reset", color = colors.warn) }
            },
            dismissButton = { TextButton(onClick = { showResetConfirm = false }) { Text("Cancel") } },
        )
    }
}

@Composable
private fun DebugSection(title: String, content: @Composable () -> Unit) {
    val colors = BriefTheme.colors
    Column(Modifier.padding(start = 18.dp, end = 18.dp, top = 14.dp)) {
        SectionHeader(title, Modifier.padding(start = 2.dp, bottom = 10.dp))
        Column(
            Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(colors.card)
                .border(1.dp, colors.border, RoundedCornerShape(14.dp)),
        ) { content() }
    }
}

@Composable
private fun Divider() {
    HorizontalDivider(thickness = 1.dp, color = BriefTheme.colors.divider)
}

@Composable
internal fun DebugToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val colors = BriefTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth().padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp), color = colors.text)
            Text(subtitle, style = MaterialTheme.typography.labelLarge.copy(fontSize = 11.sp), color = colors.textTer)
        }
        BriefToggle(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun DebugValueRow(title: String, value: String, onClick: () -> Unit) {
    val colors = BriefTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp), color = colors.text,
            modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.labelLarge, color = colors.accent)
    }
}

@Composable
private fun DebugActionRow(title: String, color: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp), color = color)
    }
}
