package com.skystone1000.briefly.presentation.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.skystone1000.briefly.presentation.Dimens.IndicatorSize
import com.skystone1000.briefly.ui.theme.BriefTheme

@Composable
fun PageIndicator(
    modifier: Modifier = Modifier,
    pageSize: Int,
    selectedPage: Int,
    selectedColor: Color = BriefTheme.colors.accent,
    unSelectedColor: Color = BriefTheme.colors.border
) {
    Row (modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween){
        repeat(pageSize){ page ->
            Box(
                modifier = Modifier
                    .size(IndicatorSize)
                    .clip(CircleShape)
                    .background(color = if (page==selectedPage) selectedColor else unSelectedColor)
            )
        }
    }
}