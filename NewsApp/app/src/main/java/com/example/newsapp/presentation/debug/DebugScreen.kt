package com.example.newsapp.presentation.debug

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newsapp.R
import com.example.newsapp.domain.debug.DebugConfig
import com.example.newsapp.presentation.common.BriefScreenTitle
import com.example.newsapp.presentation.common.BriefToggle
import com.example.newsapp.presentation.common.SectionHeader
import com.example.newsapp.ui.theme.BriefTheme

@Composable
fun DebugScreen(
    config: DebugConfig,
    onToggleSave: (Boolean) -> Unit,
    onToggleOffline: (Boolean) -> Unit,
    navigateUp: () -> Unit,
) {
    val colors = BriefTheme.colors
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

        Column(Modifier.padding(start = 18.dp, end = 18.dp, top = 6.dp)) {
            SectionHeader("MOCK & OFFLINE", Modifier.padding(start = 2.dp, bottom = 10.dp))
            Column(
                Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.card)
                    .border(1.dp, colors.border, RoundedCornerShape(14.dp)),
            ) {
                DebugToggleRow(
                    title = "Save API responses",
                    subtitle = "Capture every call into the device mock folder",
                    checked = config.saveResponses,
                    onCheckedChange = onToggleSave,
                )
                HorizontalDivider(thickness = 1.dp, color = colors.divider)
                DebugToggleRow(
                    title = "Offline mode",
                    subtitle = "Replay saved mocks instead of the network",
                    checked = config.offlineMode,
                    onCheckedChange = onToggleOffline,
                )
            }
        }
        Spacer(Modifier.size(20.dp))
    }
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp), color = colors.text)
            Text(
                subtitle,
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 11.sp),
                color = colors.textTer,
            )
        }
        BriefToggle(checked = checked, onCheckedChange = onCheckedChange)
    }
}
