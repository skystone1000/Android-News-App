package com.example.newsapp.presentation.navgraph

/**
 * All navigation destinations and nested-graph routes.
 */
sealed class Route(val route: String) {

    // Screens
    data object OnBoardingScreen : Route("onBoardingScreen")
    data object HomeScreen : Route("homeScreen")
    data object SearchScreen : Route("searchScreen")
    data object BookmarkScreen : Route("bookmarkScreen")
    data object DetailsScreen : Route("detailsScreen")
    data object NewsNavigatorScreen : Route("newsNavigator")

    // Nested graphs
    data object AppStartNavigation : Route("appStartNavigation")
    data object NewsNavigation : Route("newsNavigation")
}
