package com.example.newsapp.presentation.settings

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.newsapp.domain.model.AVAILABLE_SOURCE_IDS
import com.example.newsapp.domain.model.NewsCategories
import com.example.newsapp.domain.model.ThemeMode
import com.example.newsapp.domain.model.UserSettings
import com.example.newsapp.presentation.Dimens.MediumPadding1

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    settings: UserSettings,
    event: (SettingsEvent) -> Unit
) {
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

        SectionTitle("Data source")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AVAILABLE_SOURCE_IDS.forEach { id ->
                FilterChip(
                    selected = settings.dataSourceId == id,
                    onClick = { event(SettingsEvent.SetDataSource(id)) },
                    label = { Text(id) }
                )
            }
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

