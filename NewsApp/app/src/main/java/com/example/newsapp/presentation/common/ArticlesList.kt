package com.example.newsapp.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.newsapp.domain.model.Article
import com.example.newsapp.presentation.Dimens.ExtraSmallPadding2
import com.example.newsapp.presentation.Dimens.MediumPadding1

/** Renders a paged list of articles, handling loading/empty/error states. */
@Composable
fun ArticlesList(
    modifier: Modifier = Modifier,
    articles: LazyPagingItems<Article>,
    onClick: (Article) -> Unit
) {
    val shouldRenderList = handlePagingResult(articles)
    if (shouldRenderList) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(MediumPadding1),
            contentPadding = PaddingValues(all = ExtraSmallPadding2)
        ) {
            items(count = articles.itemCount) { index ->
                articles[index]?.let { article ->
                    ArticleCard(article = article, onClick = { onClick(article) })
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
