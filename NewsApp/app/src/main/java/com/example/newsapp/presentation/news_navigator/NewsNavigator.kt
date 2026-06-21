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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.newsapp.R
import com.example.newsapp.presentation.common.PlaceholderScreen
import com.example.newsapp.presentation.navgraph.Route
import com.example.newsapp.presentation.news_navigator.components.BottomNavigationItem
import com.example.newsapp.presentation.news_navigator.components.NewsBottomNavigation

/**
 * Hosts the bottom-nav tabs (Home / Search / Bookmark) with a nested NavHost.
 * Tab destinations are placeholders until Phase 3.
 */
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

    var selectedItem by rememberSaveable { mutableIntStateOf(0) }
    selectedItem = when (backStackState?.destination?.route) {
        Route.HomeScreen.route -> 0
        Route.SearchScreen.route -> 1
        Route.BookmarkScreen.route -> 2
        else -> selectedItem
    }

    Scaffold(
        bottomBar = {
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
                    navController.navigate(route) {
                        navController.graph.startDestinationRoute?.let { home ->
                            popUpTo(home) { saveState = true }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Route.HomeScreen.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Route.HomeScreen.route) { PlaceholderScreen(name = "Home") }
            composable(Route.SearchScreen.route) { PlaceholderScreen(name = "Search") }
            composable(Route.BookmarkScreen.route) { PlaceholderScreen(name = "Bookmark") }
        }
    }
}
