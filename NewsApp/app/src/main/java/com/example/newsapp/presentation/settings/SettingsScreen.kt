package com.example.newsapp.presentation.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.newsapp.domain.model.NewsCategories
import com.example.newsapp.domain.model.SourceCatalog
import com.example.newsapp.domain.model.ThemeMode
import com.example.newsapp.domain.model.UserSettings
import com.example.newsapp.domain.usage.UsageSnapshot
import com.example.newsapp.presentation.Dimens.MediumPadding1
import com.example.newsapp.presentation.settings.components.DataSourceCard

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    settings: UserSettings,
    configuredSourceIds: Set<String>,
    usage: Map<String, UsageSnapshot>,
    event: (SettingsEvent) -> Unit,
    navigateToHistory: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(MediumPadding1)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(MediumPadding1))

        SectionTitle("Appearance")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ThemeMode.entries.forEach { mode ->
                FilterChip(
                    selected = settings.themeMode == mode,
                    onClick = { event(SettingsEvent.SetThemeMode(mode)) },
                    label = { Text(mode.name.lowercase().replaceFirstChar { it.uppercase() }) }
                )
            }
        }
        Spacer(Modifier.height(MediumPadding1))

        SectionTitle("Data sources & API keys")
        Text(
            text = "Add a key for any provider, then pick one to power the feed. " +
                "Keys are stored encrypted on this device.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        SourceCatalog.ALL_SOURCES.forEach { meta ->
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
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        Spacer(Modifier.height(MediumPadding1))

        SwitchRow(
            title = "Personalized feed",
            subtitle = "Use only the categories you follow",
            checked = settings.personalizationEnabled,
            onCheckedChange = { event(SettingsEvent.SetPersonalization(it)) }
        )

        SectionTitle("Followed categories")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NewsCategories.ALL.forEach { category ->
                FilterChip(
                    selected = category in settings.followedCategories,
                    onClick = { event(SettingsEvent.ToggleFollowedCategory(category)) },
                    label = { Text(category) }
                )
            }
        }
        Spacer(Modifier.height(MediumPadding1))

        SwitchRow(
            title = "AI summaries",
            subtitle = "Show AI-generated summaries on article detail (Phase 5)",
            checked = settings.aiSummariesEnabled,
            onCheckedChange = { event(SettingsEvent.SetAiSummaries(it)) }
        )
        Spacer(Modifier.height(MediumPadding1))

        SectionTitle("Activity")
        NavigationRow(
            title = "Reading history",
            subtitle = "Articles you've opened",
            onClick = navigateToHistory
        )
    }
}

@Composable
private fun NavigationRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun SwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

