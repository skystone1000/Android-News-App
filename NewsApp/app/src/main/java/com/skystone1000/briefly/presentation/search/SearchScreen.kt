package com.skystone1000.briefly.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.skystone1000.briefly.domain.model.Article
import com.skystone1000.briefly.domain.model.NewsCategories
import com.skystone1000.briefly.presentation.common.ArticlesList
import com.skystone1000.briefly.presentation.common.BriefScreenTitle
import com.skystone1000.briefly.presentation.common.SectionHeader
import com.skystone1000.briefly.presentation.search.components.SearchBar
import com.skystone1000.briefly.ui.theme.BriefTheme

// Curated content for the discovery view — no trending/topic-count backend exists yet
// (see UI_REFACTOR_PLAN.md). Tapping a term runs the existing search.
private val TrendingTerms = listOf("Nippon Steel", "World Cup", "Blues album", "Spacecraft", "Lucknow fire")
private val TopicCounts = mapOf(
    "business" to 128, "technology" to 94, "sports" to 76,
    "science" to 52, "health" to 40, "entertainment" to 33,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    state: SearchState,
    event: (SearchEvent) -> Unit,
    navigateToDetails: (Article) -> Unit,
) {
    val colors = BriefTheme.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg),
    ) {
        Box(Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 6.dp)) {
            BriefScreenTitle("Search")
        }
        SearchBar(
            text = state.searchQuery,
            onValueChange = { event(SearchEvent.UpdateSearchQuery(it)) },
            onSearch = { event(SearchEvent.SearchNews) },
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp),
        )

        val results = state.articles
        if (results != null) {
            val articles = results.collectAsLazyPagingItems()
            ArticlesList(articles = articles, onClick = navigateToDetails)
        } else {
            DiscoveryContent(
                onTrendingClick = {
                    event(SearchEvent.UpdateSearchQuery(it))
                    event(SearchEvent.SearchNews)
                },
                onTopicClick = {
                    event(SearchEvent.UpdateSearchQuery(it))
                    event(SearchEvent.SearchNews)
                },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DiscoveryContent(
    onTrendingClick: (String) -> Unit,
    onTopicClick: (String) -> Unit,
) {
    val colors = BriefTheme.colors
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(16.dp))
        SectionHeader("TRENDING NOW")
        Spacer(Modifier.height(11.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            TrendingTerms.forEach { term ->
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(11.dp))
                        .background(colors.chipBg)
                        .border(1.dp, colors.border, RoundedCornerShape(11.dp))
                        .clickable { onTrendingClick(term) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.size(5.dp).clip(CircleShape).background(colors.accent))
                    Spacer(Modifier.width(6.dp))
                    Text(term, style = MaterialTheme.typography.labelLarge, color = colors.text)
                }
            }
        }

        Spacer(Modifier.height(18.dp))
        SectionHeader("BROWSE TOPICS")
        Spacer(Modifier.height(11.dp))
        val topics = NewsCategories.ALL.filter { it != NewsCategories.GENERAL }
        Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
            topics.chunked(2).forEach { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    rowItems.forEach { topic ->
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(13.dp))
                                .background(colors.card)
                                .border(1.dp, colors.border, RoundedCornerShape(13.dp))
                                .clickable { onTopicClick(topic) }
                                .padding(13.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                topic.replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.labelLarge,
                                color = colors.text,
                            )
                            Text(
                                (TopicCounts[topic] ?: 0).toString(),
                                style = MaterialTheme.typography.labelLarge,
                                color = colors.textTer,
                            )
                        }
                    }
                    if (rowItems.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}
