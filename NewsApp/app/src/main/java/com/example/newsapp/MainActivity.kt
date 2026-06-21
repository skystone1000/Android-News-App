package com.example.newsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.example.newsapp.domain.model.ThemeMode
import com.example.newsapp.presentation.MainViewModel
import com.example.newsapp.presentation.navgraph.NavGraph
import com.example.newsapp.ui.theme.NewsAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition { viewModel.splashCondition }
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val darkTheme = when (viewModel.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }
            NewsAppTheme(darkTheme = darkTheme) {
                Box(modifier = Modifier.background(color = MaterialTheme.colorScheme.background)) {
                    NavGraph(startDestination = viewModel.startDestination)
                }
            }
        }
    }
}
