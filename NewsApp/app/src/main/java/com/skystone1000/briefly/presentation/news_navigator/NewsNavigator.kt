package com.skystone1000.briefly.presentation.news_navigator

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.skystone1000.briefly.R
import com.skystone1000.briefly.domain.model.Article
import com.skystone1000.briefly.presentation.bookmark.BookmarkScreen
import com.skystone1000.briefly.presentation.bookmark.BookmarkViewModel
import com.skystone1000.briefly.presentation.debug.DebugScreen
import com.skystone1000.briefly.presentation.debug.DebugViewModel
import com.skystone1000.briefly.presentation.debug.RequestLogScreen
import com.skystone1000.briefly.presentation.debug.RequestLogViewModel
import com.skystone1000.briefly.presentation.details.DetailsScreen
import com.skystone1000.briefly.presentation.details.DetailsViewModel
import com.skystone1000.briefly.presentation.history.HistoryScreen
import com.skystone1000.briefly.presentation.history.HistoryViewModel
import com.skystone1000.briefly.presentation.home.HomeScreen
import com.skystone1000.briefly.presentation.home.HomeViewModel
import com.skystone1000.briefly.presentation.navgraph.Route
import com.skystone1000.briefly.presentation.news_navigator.components.BottomNavigationItem
import com.skystone1000.briefly.presentation.news_navigator.components.NewsBottomNavigation
import com.skystone1000.briefly.presentation.search.SearchScreen
import com.skystone1000.briefly.presentation.search.SearchViewModel
import com.skystone1000.briefly.presentation.settings.DataSourcesScreen
import com.skystone1000.briefly.presentation.settings.SettingsScreen
import com.skystone1000.briefly.presentation.settings.SettingsViewModel

private const val ARTICLE_KEY = "article"

/** Hosts the bottom-nav tabs (Home / Search / Bookmark) plus the full-screen Details route. */
@Composable
fun NewsNavigator() {

    val bottomNavigationItems = remember {
        listOf(
            BottomNavigationItem(icon = R.drawable.ic_home, text = "Home"),
            BottomNavigationItem(icon = R.drawable.ic_search, text = "Search"),
            BottomNavigationItem(icon = R.drawable.ic_bookmark, text = "Saved"),
            BottomNavigationItem(icon = R.drawable.ic_preferences, text = "Settings")
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
        Route.SettingsScreen.route -> 3
        else -> selectedItem
    }

    val isBottomBarVisible = currentRoute in setOf(
        Route.HomeScreen.route,
        Route.SearchScreen.route,
        Route.BookmarkScreen.route,
        Route.SettingsScreen.route
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
                            3 -> Route.SettingsScreen.route
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
                val settings by viewModel.settings.collectAsState()
                HomeScreen(
                    articles = articles,
                    settings = settings,
                    onCategorySelected = viewModel::selectCategory,
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
            composable(Route.SettingsScreen.route) {
                val viewModel: SettingsViewModel = hiltViewModel()
                val settings by viewModel.settings.collectAsState()
                val configuredSourceIds by viewModel.configuredSourceIds.collectAsState()
                SettingsScreen(
                    settings = settings,
                    configuredSourceIds = configuredSourceIds,
                    event = viewModel::onEvent,
                    navigateToDataSources = { navController.navigate(Route.DataSourcesScreen.route) },
                    navigateToHistory = { navController.navigate(Route.HistoryScreen.route) },
                    navigateToDebug = { navController.navigate(Route.DebugScreen.route) }
                )
            }
            composable(Route.DebugScreen.route) {
                val viewModel: DebugViewModel = hiltViewModel()
                val config by viewModel.config.collectAsState()
                DebugScreen(
                    config = config,
                    sideEffect = viewModel.sideEffect,
                    onToggleSave = viewModel::setSaveResponses,
                    onToggleOffline = viewModel::setOfflineMode,
                    onToggleForceError = viewModel::setForceError,
                    onCycleLatency = viewModel::cycleLatency,
                    onClearMocks = viewModel::clearMocks,
                    onResetState = viewModel::resetAppState,
                    onSideEffectShown = viewModel::consumeSideEffect,
                    navigateToRequestLog = { navController.navigate(Route.RequestLogScreen.route) },
                    navigateUp = { navController.navigateUp() }
                )
            }
            composable(Route.RequestLogScreen.route) {
                val viewModel: RequestLogViewModel = hiltViewModel()
                val entries by viewModel.entries.collectAsState()
                RequestLogScreen(
                    entries = entries,
                    onClear = viewModel::clear,
                    navigateUp = { navController.navigateUp() }
                )
            }
            composable(Route.DataSourcesScreen.route) {
                val viewModel: SettingsViewModel = hiltViewModel()
                val settings by viewModel.settings.collectAsState()
                val configuredSourceIds by viewModel.configuredSourceIds.collectAsState()
                val usage by viewModel.usage.collectAsState()
                DataSourcesScreen(
                    settings = settings,
                    configuredSourceIds = configuredSourceIds,
                    usage = usage,
                    event = viewModel::onEvent,
                    navigateUp = { navController.navigateUp() }
                )
            }
            composable(Route.HistoryScreen.route) {
                val viewModel: HistoryViewModel = hiltViewModel()
                HistoryScreen(
                    articles = viewModel.articles,
                    onClearHistory = viewModel::clearHistory,
                    navigateUp = { navController.navigateUp() },
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
                        aiState = viewModel.aiState,
                        isBookmarked = viewModel.isBookmarked,
                        event = viewModel::onEvent,
                        onRequestInsight = {
                            viewModel.syncBookmark(article)
                            viewModel.recordHistory(article)
                            viewModel.loadInsightIfEnabled(article)
                        },
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
