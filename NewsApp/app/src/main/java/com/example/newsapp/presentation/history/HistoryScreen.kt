package com.example.newsapp.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.newsapp.R
import com.example.newsapp.domain.model.Article
import com.example.newsapp.presentation.common.ArticleRow
import com.example.newsapp.presentation.common.EmptyScreen
import com.example.newsapp.ui.theme.BriefTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    articles: List<Article>,
    onClearHistory: () -> Unit,
    navigateUp: () -> Unit,
    navigateToDetails: (Article) -> Unit
) {
    val colors = BriefTheme.colors
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
    ) {
        TopAppBar(
            title = { Text("Reading history") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = colors.text,
                actionIconContentColor = colors.text,
                navigationIconContentColor = colors.text
            ),
            navigationIcon = {
                IconButton(onClick = navigateUp) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back_arrow),
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                if (articles.isNotEmpty()) {
                    IconButton(onClick = onClearHistory) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Clear history"
                        )
                    }
                }
            }
        )

        if (articles.isEmpty()) {
            EmptyScreen()
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(count = articles.size) { index ->
                    val article = articles[index]
                    if (index > 0) HorizontalDivider(thickness = 1.dp, color = colors.divider)
                    ArticleRow(
                        article = article,
                        kicker = article.source.name,
                        time = article.publishedAt,
                        onClick = { navigateToDetails(article) },
                    )
                }
            }
        }
    }
}
