package com.skystone1000.briefly.presentation.news_navigator.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.skystone1000.briefly.presentation.Dimens.IconSize
import com.skystone1000.briefly.ui.theme.BriefTheme

@Composable
fun NewsBottomNavigation(
    items: List<BottomNavigationItem>,
    selectedItem: Int,
    onItemClick: (Int) -> Unit
) {
    val colors = BriefTheme.colors
    Column(
        Modifier
            .background(colors.navBg)
            .navigationBarsPadding(),
    ) {
        HorizontalDivider(thickness = 1.dp, color = colors.divider)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 9.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            items.forEachIndexed { index, item ->
                val active = index == selectedItem
                val tint = if (active) colors.accent else colors.textTer
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onItemClick(index) },
                ) {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.text,
                        tint = tint,
                        modifier = Modifier.size(IconSize),
                    )
                    Text(
                        text = item.text,
                        style = MaterialTheme.typography.labelSmall,
                        color = tint,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }
    }
}
