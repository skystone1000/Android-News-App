package com.example.newsapp.presentation.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.newsapp.presentation.Dimens.ButtonRadius
import com.example.newsapp.ui.theme.BriefTheme

/** Filled emerald primary button (Brief design system). */
@Composable
fun BriefButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = BriefTheme.colors
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.accent,
            contentColor = colors.onAccent,
        ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 11.dp),
        shape = RoundedCornerShape(ButtonRadius),
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

/** Surface button with hairline border (secondary action, e.g. "Save"). */
@Composable
fun BriefSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = BriefTheme.colors
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = colors.surface,
            contentColor = colors.text,
        ),
        border = BorderStroke(1.dp, colors.border),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 11.dp),
        shape = RoundedCornerShape(ButtonRadius),
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

/** Emerald text-only action (e.g. "Get a key"). */
@Composable
fun BriefTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TextButton(onClick = onClick, modifier = modifier) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = BriefTheme.colors.accent,
        )
    }
}

// --- Legacy names kept for the onboarding screen, restyled to Brief ---

@Composable
fun NewsButton(text: String, onClick: () -> Unit) = BriefButton(text = text, onClick = onClick)

@Composable
fun NewsTextButton(text: String, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = BriefTheme.colors.textSec,
        )
    }
}
