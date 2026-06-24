package com.example.newsapp.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newsapp.presentation.Dimens.TabUnderline
import com.example.newsapp.ui.theme.BriefTheme

/** Scrollable underline category tabs used on Home. */
@Composable
fun CategoryTabRow(
    tabs: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = BriefTheme.colors
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        tabs.forEachIndexed { i, label ->
            val active = i == selectedIndex
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onSelect(i) }
                    .padding(end = 20.dp),
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.5.sp),
                    color = if (active) colors.text else colors.textTer,
                )
                Spacer(Modifier.height(5.dp))
                Spacer(
                    Modifier
                        .width(20.dp)
                        .height(TabUnderline)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (active) colors.accent else Color.Transparent),
                )
            }
        }
    }
}
