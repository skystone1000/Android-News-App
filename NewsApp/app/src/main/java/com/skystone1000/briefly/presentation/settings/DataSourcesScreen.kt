package com.skystone1000.briefly.presentation.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.skystone1000.briefly.R
import com.skystone1000.briefly.domain.model.SourceCatalog
import com.skystone1000.briefly.domain.model.UserSettings
import com.skystone1000.briefly.domain.usage.UsageSnapshot
import com.skystone1000.briefly.presentation.common.BriefScreenTitle
import com.skystone1000.briefly.presentation.settings.components.DataSourceCard
import com.skystone1000.briefly.ui.theme.BriefTheme

/** Standalone screen (Settings → "Manage sources & keys") for per-provider keys + usage. */
@Composable
fun DataSourcesScreen(
    settings: UserSettings,
    configuredSourceIds: Set<String>,
    usage: Map<String, UsageSnapshot>,
    event: (SettingsEvent) -> Unit,
    navigateUp: () -> Unit,
) {
    val colors = BriefTheme.colors
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg),
    ) {
        Row(
            modifier = Modifier.padding(start = 14.dp, end = 18.dp, top = 8.dp),
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
            BriefScreenTitle("Data sources")
        }
        Text(
            text = "Add provider keys to power your feed. Keys are stored encrypted on this device.",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textTer,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 2.dp, bottom = 14.dp),
        )

        LazyColumn(modifier = Modifier.padding(horizontal = 18.dp)) {
            items(count = SourceCatalog.ALL_SOURCES.size) { index ->
                val meta = SourceCatalog.ALL_SOURCES[index]
                DataSourceCard(
                    metadata = meta,
                    isActive = settings.dataSourceId == meta.id,
                    isConfigured = meta.id in configuredSourceIds,
                    usage = usage[meta.id],
                    onSetKey = { key -> event(SettingsEvent.SetApiKey(meta.id, key)) },
                    onClearKey = { event(SettingsEvent.ClearApiKey(meta.id)) },
                    onSelect = { event(SettingsEvent.SetDataSource(meta.id)) },
                    onGetKey = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(meta.signupUrl))
                        if (intent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(intent)
                        }
                    },
                    modifier = Modifier.padding(bottom = 11.dp),
                )
            }
            item { Spacer(Modifier.height(10.dp)) }
        }
    }
}
