package com.example.newsapp.presentation.common

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.newsapp.presentation.Dimens.ChipRadius
import com.example.newsapp.presentation.Dimens.ToggleHeight
import com.example.newsapp.presentation.Dimens.ToggleKnob
import com.example.newsapp.presentation.Dimens.ToggleWidth
import com.example.newsapp.ui.theme.BriefTheme

/** Pill chip; filled (selected) or outlined (unselected). */
@Composable
fun BriefChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = BriefTheme.colors
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(ChipRadius))
            .background(if (selected) colors.accent else Color.Transparent)
            .then(
                if (selected) Modifier
                else Modifier.border(1.dp, colors.border, RoundedCornerShape(ChipRadius))
            )
            .clickable { onClick() }
            .padding(horizontal = 13.dp, vertical = 7.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) colors.onAccent else colors.textSec,
        )
    }
}

/** 38x22 emerald switch matching the Figma toggle. */
@Composable
fun BriefToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = BriefTheme.colors
    val knobOffset by animateDpAsState(if (checked) ToggleWidth - ToggleKnob - 2.dp else 2.dp, label = "knob")
    Box(
        modifier = modifier
            .size(ToggleWidth, ToggleHeight)
            .clip(CircleShape)
            .background(if (checked) colors.accent else colors.toggleOff)
            .clickable { onCheckedChange(!checked) },
    ) {
        Box(
            modifier = Modifier
                .padding(start = knobOffset, top = 2.dp)
                .size(ToggleKnob)
                .clip(CircleShape)
                .background(Color.White),
        )
    }
}

/** Light/Dark/System style segmented control. */
@Composable
fun SegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = BriefTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(13.dp))
            .background(colors.chipBg)
            .border(1.dp, colors.border, RoundedCornerShape(13.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        options.forEachIndexed { i, label ->
            val active = i == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (active) colors.surface else Color.Transparent)
                    .clickable { onSelect(i) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (active) colors.text else colors.textSec,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

/** Tracked overline section header (e.g. "TRENDING NOW", "APPEARANCE"). */
@Composable
fun SectionHeader(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.06.em),
        color = BriefTheme.colors.textTer,
        modifier = modifier,
    )
}

/** Large display screen title (Search / Saved / Settings) at 24sp. */
@Composable
fun BriefScreenTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.displaySmall.copy(fontSize = 24.sp),
        color = BriefTheme.colors.text,
        modifier = modifier,
    )
}
