package com.example.newsapp.presentation.common

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.newsapp.presentation.Dimens.ArticleCardSize
import com.example.newsapp.presentation.Dimens.ExtraSmallPadding2
import com.example.newsapp.presentation.Dimens.MediumPadding1

@Composable
fun ShimmerEffectColumn(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        repeat(SHIMMER_ITEM_COUNT) {
            ArticleCardShimmerEffect(modifier = Modifier.padding(horizontal = MediumPadding1))
        }
    }
}

@Composable
private fun ArticleCardShimmerEffect(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )
    val shimmerColor = Color.Gray.copy(alpha = alpha)

    Row(modifier = modifier.padding(vertical = ExtraSmallPadding2)) {
        Box(
            modifier = Modifier
                .size(ArticleCardSize)
                .clip(RoundedCornerShape(12.dp))
                .background(shimmerColor)
        )
        Column(
            modifier = Modifier
                .padding(horizontal = ExtraSmallPadding2)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .height(30.dp)
                    .background(shimmerColor)
            )
        }
    }
}

private const val SHIMMER_ITEM_COUNT = 10
