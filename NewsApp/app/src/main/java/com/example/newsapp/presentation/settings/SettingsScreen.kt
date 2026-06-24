package com.example.newsapp.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newsapp.domain.model.NewsCategories
import com.example.newsapp.domain.model.SourceCatalog
import com.example.newsapp.domain.model.ThemeMode
import com.example.newsapp.domain.model.UserSettings
import com.example.newsapp.presentation.common.BriefChip
import com.example.newsapp.presentation.common.BriefScreenTitle
import com.example.newsapp.presentation.common.BriefToggle
import com.example.newsapp.presentation.common.SectionHeader
import com.example.newsapp.ui.theme.BriefTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    settings: UserSettings,
    configuredSourceIds: Set<String>,
    event: (SettingsEvent) -> Unit,
    navigateToDataSources: () -> Unit,
    navigateToHistory: () -> Unit,
) {
    val colors = BriefTheme.colors
    val themeOrder = listOf(ThemeMode.LIGHT, ThemeMode.DARK, ThemeMode.SYSTEM)
    val activeSources = configuredSourceIds.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
            .verticalScroll(rememberScrollState()),
    ) {
        Box(Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 14.dp)) {
            BriefScreenTitle("Settings")
        }

        // Appearance
        Column(Modifier.padding(horizontal = 18.dp)) {
            SectionHeader("APPEARANCE", Modifier.padding(start = 2.dp, bottom = 10.dp))
            com.example.newsapp.presentation.common.SegmentedControl(
                options = listOf("Light", "Dark", "System"),
                selectedIndex = themeOrder.indexOf(settings.themeMode).coerceAtLeast(0),
                onSelect = { event(SettingsEvent.SetThemeMode(themeOrder[it])) },
            )
        }

        // Data sources entry row
        Column(Modifier.padding(start = 18.dp, end = 18.dp, top = 18.dp)) {
            SectionHeader("DATA SOURCES & API KEYS", Modifier.padding(start = 2.dp, bottom = 10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.card)
                    .border(1.dp, colors.border, RoundedCornerShape(14.dp))
                    .clickable(onClick = navigateToDataSources)
                    .padding(15.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(colors.accentSoft),
                )
                Spacer(Modifier.size(13.dp))
                Column(Modifier.weight(1f)) {
                    Text("Manage sources & keys", style = MaterialTheme.typography.labelLarge, color = colors.text)
                    Text(
                        "$activeSources configured · ${SourceCatalog.ALL_SOURCES.size - activeSources} need a key",
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 11.5.sp),
                        color = colors.textTer,
                    )
                }
                Text("›", style = MaterialTheme.typography.headlineMedium, color = colors.textTer)
            }
        }

        // Preferences
        Column(Modifier.padding(start = 18.dp, end = 18.dp, top = 14.dp)) {
            SectionHeader("PREFERENCES", Modifier.padding(start = 2.dp, bottom = 10.dp))
            Column(
                Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.card)
                    .border(1.dp, colors.border, RoundedCornerShape(14.dp)),
            ) {
                ToggleRow(
                    title = "Personalized feed",
                    subtitle = "Only categories you follow",
                    checked = settings.personalizationEnabled,
                    onCheckedChange = { event(SettingsEvent.SetPersonalization(it)) },
                )
                HorizontalDivider(thickness = 1.dp, color = colors.divider)
                ToggleRow(
                    title = "AI summaries",
                    subtitle = "Show on article detail",
                    checked = settings.aiSummariesEnabled,
                    onCheckedChange = { event(SettingsEvent.SetAiSummaries(it)) },
                )
                HorizontalDivider(thickness = 1.dp, color = colors.divider)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = navigateToHistory)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Reading history", style = MaterialTheme.typography.labelLarge, color = colors.text)
                        Text(
                            "Articles you've opened",
                            style = MaterialTheme.typography.labelLarge.copy(fontSize = 11.sp),
                            color = colors.textTer,
                        )
                    }
                    Text("›", style = MaterialTheme.typography.headlineMedium, color = colors.textTer)
                }
            }
        }

        // Followed categories
        Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 14.dp)) {
            SectionHeader("FOLLOWED CATEGORIES", Modifier.padding(start = 2.dp, bottom = 11.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                NewsCategories.ALL.forEach { category ->
                    BriefChip(
                        label = category.replaceFirstChar { it.uppercase() },
                        selected = category in settings.followedCategories,
                        onClick = { event(SettingsEvent.ToggleFollowedCategory(category)) },
                    )
                }
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun ToggleRow(
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
