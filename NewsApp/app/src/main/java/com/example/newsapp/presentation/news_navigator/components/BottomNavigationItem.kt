package com.example.newsapp.presentation.news_navigator.components

import androidx.annotation.DrawableRes

/** A single bottom-navigation tab (icon + label). */
data class BottomNavigationItem(
    @DrawableRes val icon: Int,
    val text: String
)
