package com.example.newsapp.presentation.common

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.newsapp.domain.model.Article
import com.example.newsapp.presentation.Dimens.MediumPadding1
import com.example.newsapp.ui.theme.BriefTheme

/** Renders a paged list of articles as [ArticleRow]s, handling loading/empty/error states. */
@Composable
fun ArticlesList(
    modifier: Modifier = Modifier,
    articles: LazyPagingItems<Article>,
    kicker: (Article) -> String? = { it.source.name },
    onClick: (Article) -> Unit,
) {
    val shouldRenderList = handlePagingResult(articles)
    if (shouldRenderList) {
        val divider = BriefTheme.colors.divider
        LazyColumn(modifier = modifier.fillMaxSize()) {
            items(count = articles.itemCount) { index ->
                articles[index]?.let { article ->
                    if (index > 0) {
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 1.dp,
                            color = divider,
                        )
                    }
                    ArticleRow(
                        article = article,
                        kicker = kicker(article),
                        time = article.publishedAt,
                        onClick = { onClick(article) },
                    )
                }
            }
        }
    }
}

@Composable
private fun handlePagingResult(articles: LazyPagingItems<Article>): Boolean {
    val loadState = articles.loadState
    val error = when {
        loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
        loadState.append is LoadState.Error -> loadState.append as LoadState.Error
        loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
        else -> null
    }

    return when {
        loadState.refresh is LoadState.Loading -> {
            ShimmerEffectColumn(modifier = Modifier.fillMaxSize().padding(top = MediumPadding1))
            false
        }

        error != null -> {
            EmptyScreen(error = error)
            false
        }

        articles.itemCount == 0 && loadState.refresh is LoadState.NotLoading -> {
            EmptyScreen()
            false
        }

        else -> true
    }
}
