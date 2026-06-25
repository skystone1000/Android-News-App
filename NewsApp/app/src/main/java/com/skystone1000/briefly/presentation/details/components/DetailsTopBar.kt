package com.skystone1000.briefly.presentation.details.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.skystone1000.briefly.R
import com.skystone1000.briefly.ui.theme.BriefTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsTopBar(
    isSpeaking: Boolean,
    isBookmarked: Boolean,
    onListenClick: () -> Unit,
    onShareClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onBrowsingClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val colors = BriefTheme.colors
    TopAppBar(
        title = {},
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            actionIconContentColor = colors.textSec,
            navigationIconContentColor = colors.text
        ),
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back_arrow),
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            // Listen: a play triangle when idle, a stop square while reading aloud.
            IconButton(onClick = onListenClick) {
                if (isSpeaking) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_stop),
                        contentDescription = "Stop reading aloud",
                        tint = colors.accent,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Listen to article",
                        tint = colors.textSec,
                    )
                }
            }
            IconButton(onClick = onShareClick) {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = "Share article"
                )
            }
            // Bookmark: filled + accent when saved, outline when not.
            IconButton(onClick = onBookmarkClick) {
                Icon(
                    painter = painterResource(
                        id = if (isBookmarked) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark,
                    ),
                    contentDescription = if (isBookmarked) "Remove bookmark" else "Save article",
                    tint = if (isBookmarked) colors.accent else colors.textSec,
                )
            }
            IconButton(onClick = onBrowsingClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_network),
                    contentDescription = "Open in browser"
                )
            }
        }
    )
}
