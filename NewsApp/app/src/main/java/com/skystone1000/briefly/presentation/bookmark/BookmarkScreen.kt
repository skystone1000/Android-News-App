package com.skystone1000.briefly.presentation.bookmark

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.skystone1000.briefly.R
import com.skystone1000.briefly.domain.model.Article
import com.skystone1000.briefly.presentation.common.ArticleRow
import com.skystone1000.briefly.presentation.common.BriefChip
import com.skystone1000.briefly.presentation.common.BriefScreenTitle
import com.skystone1000.briefly.presentation.common.EmptyScreen
import com.skystone1000.briefly.ui.theme.BriefTheme

@Composable
fun BookmarkScreen(
    state: BookmarkState,
    navigateToDetails: (Article) -> Unit,
) {
    val colors = BriefTheme.colors
    // 0 = All, 1 = Unread (saved articles not yet opened — i.e. not in reading history).
    var filterIndex by remember { mutableIntStateOf(0) }
    val visibleArticles = if (filterIndex == 1) {
        state.articles.filter { it.url !in state.readUrls }
    } else {
        state.articles
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            BriefScreenTitle("Saved")
            Text(
                text = "${state.articles.size} articles",
                style = MaterialTheme.typography.labelLarge,
                color = colors.textTer,
                modifier = Modifier.padding(bottom = 3.dp),
            )
        }

        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            BriefChip("All", selected = filterIndex == 0, onClick = { filterIndex = 0 })
            BriefChip("Unread", selected = filterIndex == 1, onClick = { filterIndex = 1 })
        }
        Spacer(Modifier.height(4.dp))

        if (visibleArticles.isEmpty()) {
            EmptyScreen()
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(count = visibleArticles.size) { index ->
                    val article = visibleArticles[index]
                    if (index > 0) HorizontalDivider(thickness = 1.dp, color = colors.divider)
                    ArticleRow(
                        article = article,
                        thumbnailLeading = true,
                        kicker = article.source.name,
                        onClick = { navigateToDetails(article) },
                        trailing = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_bookmark),
                                contentDescription = "Saved",
                                tint = colors.accent,
                                modifier = Modifier.size(17.dp),
                            )
                        },
                    )
                }
            }
        }
    }
}
