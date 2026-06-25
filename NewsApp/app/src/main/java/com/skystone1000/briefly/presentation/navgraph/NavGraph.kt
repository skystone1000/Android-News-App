package com.skystone1000.briefly.presentation.navgraph

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.skystone1000.briefly.presentation.news_navigator.NewsNavigator
import com.skystone1000.briefly.presentation.onboarding.OnBoardingEvent
import com.skystone1000.briefly.presentation.onboarding.OnBoardingScreen
import com.skystone1000.briefly.presentation.onboarding.OnBoardingViewModel

/**
 * Top-level navigation: an app-start graph (onboarding) and the main news graph.
 * [startDestination] is decided by [com.skystone1000.briefly.presentation.MainViewModel].
 */
@Composable
fun NavGraph(startDestination: String) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {

        navigation(
            route = Route.AppStartNavigation.route,
            startDestination = Route.OnBoardingScreen.route
        ) {
            composable(route = Route.OnBoardingScreen.route) {
                val viewModel: OnBoardingViewModel = hiltViewModel()
                OnBoardingScreen(event = { event ->
                    viewModel.onEvent(event)
                    if (event is OnBoardingEvent.SaveAppEntry) {
                        navController.navigate(Route.NewsNavigation.route) {
                            popUpTo(Route.AppStartNavigation.route) { inclusive = true }
                        }
                    }
                })
            }
        }

        navigation(
            route = Route.NewsNavigation.route,
            startDestination = Route.NewsNavigatorScreen.route
        ) {
            composable(route = Route.NewsNavigatorScreen.route) {
                NewsNavigator()
            }
        }
    }
}
