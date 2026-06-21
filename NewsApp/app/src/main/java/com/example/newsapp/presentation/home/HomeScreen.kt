package com.example.newsapp.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.example.newsapp.R
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.model.NewsCategories
import com.example.newsapp.domain.model.UserSettings
import com.example.newsapp.presentation.Dimens.MediumPadding1
import com.example.newsapp.presentation.common.ArticlesList
import com.example.newsapp.presentation.home.components.CategoryChips

@Composable
fun HomeScreen(
    articles: LazyPagingItems<Article>,
    settings: UserSettings,
    onCategorySelected: (String) -> Unit,
    navigateToDetails: (Article) -> Unit
) {
    val categories = if (settings.personalizationEnabled && settings.followedCategories.isNotEmpty()) {
        NewsCategories.ALL.filter { it in settings.followedCategories }
    } else {
        NewsCategories.ALL
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = MediumPadding1)
            .statusBarsPadding()
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = MediumPadding1)
                .size(width = 150.dp, height = 30.dp)
        )
        Spacer(modifier = Modifier.height(MediumPadding1))
        CategoryChips(
            categories = categories,
            selected = settings.selectedCategory,
            onSelected = onCategorySelected,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MediumPadding1)
        )
        Spacer(modifier = Modifier.height(MediumPadding1))
        ArticlesList(
            modifier = Modifier.padding(horizontal = MediumPadding1),
            articles = articles,
            onClick = navigateToDetails
        )
    }
}
