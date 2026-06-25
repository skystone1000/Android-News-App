package com.skystone1000.briefly.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.skystone1000.briefly.ui.theme.BriefTheme
import com.skystone1000.briefly.ui.theme.Schibsted

/**
 * The Brief brand mark: an emerald rounded square with three descending bars
 * ("a story trimmed to its essentials"). Drawn in Compose so it inverts with the theme.
 * The launcher icon (mipmap) mirrors this mark in emerald + white bars.
 */
@Composable
fun BriefMark(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
) {
    val colors = BriefTheme.colors
    val bar = size * 0.5f
    val barH = (size.value * 0.083f).dp
    Column(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.33f))
            .background(colors.accent),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(Modifier.width(bar).height(barH).clip(RoundedCornerShape(1.dp)).background(colors.onAccent))
        Spacer(Modifier.height(barH * 1.25f))
        Spacer(Modifier.width(bar).height(barH).clip(RoundedCornerShape(1.dp)).background(colors.onAccent))
        Spacer(Modifier.height(barH * 1.25f))
        Box(Modifier.width(bar), contentAlignment = Alignment.CenterStart) {
            Spacer(Modifier.width(bar * 0.58f).height(barH).clip(RoundedCornerShape(1.dp)).background(colors.onAccent))
        }
    }
}

/** Brand mark + "Briefly" wordmark, used in headers and onboarding. */
@Composable
fun BriefWordmark(
    modifier: Modifier = Modifier,
    markSize: Dp = 24.dp,
    textSize: TextUnit = 20.sp,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        BriefMark(size = markSize)
        Spacer(Modifier.width(8.dp))
        Text(
            text = "Briefly",
            color = BriefTheme.colors.text,
            style = TextStyle(
                fontFamily = Schibsted,
                fontWeight = FontWeight(800),
                fontSize = textSize,
                letterSpacing = (-0.03).em,
            ),
        )
    }
}
