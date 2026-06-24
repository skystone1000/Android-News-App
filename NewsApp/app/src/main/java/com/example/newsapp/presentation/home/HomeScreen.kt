package com.example.newsapp.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.model.NewsCategories
import com.example.newsapp.domain.model.UserSettings
import com.example.newsapp.presentation.common.ArticleRow
import com.example.newsapp.presentation.common.BriefWordmark
import com.example.newsapp.presentation.common.CategoryTabRow
import com.example.newsapp.presentation.common.EmptyScreen
import com.example.newsapp.presentation.common.FeaturedCard
import com.example.newsapp.presentation.common.ShimmerEffectColumn
import com.example.newsapp.ui.theme.BriefTheme

@Composable
fun HomeScreen(
    articles: LazyPagingItems<Article>,
    settings: UserSettings,
    onCategorySelected: (String) -> Unit,
    navigateToDetails: (Article) -> Unit,
) {
    val colors = BriefTheme.colors
    val categories = if (settings.personalizationEnabled && settings.followedCategories.isNotEmpty()) {
        NewsCategories.ALL.filter { it in settings.followedCategories }
    } else {
        NewsCategories.ALL
    }
    val selectedIndex = categories.indexOf(settings.selectedCategory).coerceAtLeast(0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg),
    ) {
        // Header: wordmark + avatar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BriefWordmark()
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(colors.chipBg)
                    .border(1.dp, colors.border, CircleShape),
            )
        }

        CategoryTabRow(
            tabs = categories.map { it.replaceFirstChar { c -> c.uppercase() } },
            selectedIndex = selectedIndex,
            onSelect = { onCategorySelected(categories[it]) },
            modifier = Modifier.padding(bottom = 12.dp),
        )

        val kicker = categories[selectedIndex].uppercase()
        HomeFeed(
            articles = articles,
            kicker = kicker,
            divider = colors.divider,
            navigateToDetails = navigateToDetails,
        )
    }
}

@Composable
private fun HomeFeed(
    articles: LazyPagingItems<Article>,
    kicker: String,
    divider: androidx.compose.ui.graphics.Color,
    navigateToDetails: (Article) -> Unit,
) {
    val refresh = articles.loadState.refresh
    when {
        refresh is LoadState.Loading -> ShimmerEffectColumn(Modifier.fillMaxSize().padding(top = 24.dp))
        refresh is LoadState.Error -> EmptyScreen(error = refresh)
        articles.itemCount == 0 && refresh is LoadState.NotLoading -> EmptyScreen()
        else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(count = articles.itemCount) { index ->
                val article = articles[index] ?: return@items
                if (index == 0) {
                    FeaturedCard(
                        article = article,
                        badge = kicker,
                        time = article.publishedAt,
                        onClick = { navigateToDetails(article) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    )
                } else {
                    HorizontalDivider(thickness = 1.dp, color = divider)
                    ArticleRow(
                        article = article,
                        kicker = kicker,
                        time = article.publishedAt,
                        onClick = { navigateToDetails(article) },
                    )
                }
            }
            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}
