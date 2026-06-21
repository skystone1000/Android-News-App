package com.example.newsapp.presentation.news_navigator

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.newsapp.R
import com.example.newsapp.domain.model.Article
import com.example.newsapp.presentation.bookmark.BookmarkScreen
import com.example.newsapp.presentation.bookmark.BookmarkViewModel
import com.example.newsapp.presentation.details.DetailsScreen
import com.example.newsapp.presentation.details.DetailsViewModel
import com.example.newsapp.presentation.home.HomeScreen
import com.example.newsapp.presentation.home.HomeViewModel
import com.example.newsapp.presentation.navgraph.Route
import com.example.newsapp.presentation.news_navigator.components.BottomNavigationItem
import com.example.newsapp.presentation.news_navigator.components.NewsBottomNavigation
import com.example.newsapp.presentation.search.SearchScreen
import com.example.newsapp.presentation.search.SearchViewModel

private const val ARTICLE_KEY = "article"

/** Hosts the bottom-nav tabs (Home / Search / Bookmark) plus the full-screen Details route. */
@Composable
fun NewsNavigator() {

    val bottomNavigationItems = remember {
        listOf(
            BottomNavigationItem(icon = R.drawable.ic_home, text = "Home"),
            BottomNavigationItem(icon = R.drawable.ic_search, text = "Search"),
            BottomNavigationItem(icon = R.drawable.ic_bookmark, text = "Bookmark")
        )
    }

    val navController = rememberNavController()
    val backStackState by navController.currentBackStackEntryAsState()
    val currentRoute = backStackState?.destination?.route

    var selectedItem by rememberSaveable { mutableIntStateOf(0) }
    selectedItem = when (currentRoute) {
        Route.HomeScreen.route -> 0
        Route.SearchScreen.route -> 1
        Route.BookmarkScreen.route -> 2
        else -> selectedItem
    }

    val isBottomBarVisible = currentRoute in setOf(
        Route.HomeScreen.route,
        Route.SearchScreen.route,
        Route.BookmarkScreen.route
    )

    Scaffold(
        bottomBar = {
            if (isBottomBarVisible) {
                NewsBottomNavigation(
                    items = bottomNavigationItems,
                    selectedItem = selectedItem,
                    onItemClick = { index ->
                        val route = when (index) {
                            0 -> Route.HomeScreen.route
                            1 -> Route.SearchScreen.route
                            2 -> Route.BookmarkScreen.route
                            else -> Route.HomeScreen.route
                        }
                        navigateToTab(navController, route)
                    }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Route.HomeScreen.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Route.HomeScreen.route) {
                val viewModel: HomeViewModel = hiltViewModel()
                val articles = viewModel.news.collectAsLazyPagingItems()
                HomeScreen(
                    articles = articles,
                    navigateToDetails = { navigateToDetails(navController, it) }
                )
            }
            composable(Route.SearchScreen.route) {
                val viewModel: SearchViewModel = hiltViewModel()
                SearchScreen(
                    state = viewModel.state,
                    event = viewModel::onEvent,
                    navigateToDetails = { navigateToDetails(navController, it) }
                )
            }
            composable(Route.BookmarkScreen.route) {
                val viewModel: BookmarkViewModel = hiltViewModel()
                BookmarkScreen(
                    state = viewModel.state,
                    navigateToDetails = { navigateToDetails(navController, it) }
                )
            }
            composable(Route.DetailsScreen.route) {
                val viewModel: DetailsViewModel = hiltViewModel()
                val article = navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<Article>(ARTICLE_KEY)
                if (article != null) {
                    DetailsScreen(
                        article = article,
                        sideEffect = viewModel.sideEffect,
                        event = viewModel::onEvent,
                        navigateUp = { navController.navigateUp() }
                    )
                }
            }
        }
    }
}

private fun navigateToTab(navController: NavController, route: String) {
    navController.navigate(route) {
        navController.graph.startDestinationRoute?.let { home ->
            popUpTo(home) { saveState = true }
        }
        launchSingleTop = true
        restoreState = true
    }
}

private fun navigateToDetails(navController: NavController, article: Article) {
    navController.currentBackStackEntry?.savedStateHandle?.set(ARTICLE_KEY, article)
    navController.navigate(Route.DetailsScreen.route)
}
