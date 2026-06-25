package com.skystone1000.briefly.presentation.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.skystone1000.briefly.domain.model.SourceMetadata
import com.skystone1000.briefly.domain.usage.UsageSnapshot
import com.skystone1000.briefly.presentation.common.BriefButton
import com.skystone1000.briefly.presentation.common.BriefTextButton
import com.skystone1000.briefly.ui.theme.BriefTheme

/**
 * One provider card (Brief design): name + status, a usage meter (when configured), a masked
 * key field (Save / Clear), a "Get a key" link, and an active-source selector enabled once a key exists.
 */
@Composable
fun DataSourceCard(
    metadata: SourceMetadata,
    isActive: Boolean,
    isConfigured: Boolean,
    usage: UsageSnapshot?,
    onSetKey: (String) -> Unit,
    onClearKey: () -> Unit,
    onSelect: () -> Unit,
    onGetKey: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = BriefTheme.colors
    var keyInput by rememberSaveable(metadata.id) { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.card)
            .border(1.dp, colors.border, RoundedCornerShape(16.dp))
            .padding(15.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = metadata.displayName,
                style = MaterialTheme.typography.titleMedium,
                color = colors.text,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = when {
                    isActive -> "Active"
                    isConfigured -> "Ready"
                    else -> "No key"
                },
                style = MaterialTheme.typography.labelLarge,
                color = if (isConfigured) colors.good else colors.textTer,
            )
        }

        if (isConfigured && usage != null) {
            Spacer(Modifier.size(12.dp))
            UsageMeter(snapshot = usage)
        }

        Spacer(Modifier.size(13.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(11.dp))
                .border(1.dp, colors.border, RoundedCornerShape(11.dp))
                .background(colors.surface)
                .padding(horizontal = 13.dp, vertical = 12.dp),
        ) {
            if (keyInput.isEmpty()) {
                Text(
                    text = "API key (${metadata.keyParamHint})",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textTer,
                )
            }
            BasicTextField(
                value = keyInput,
                onValueChange = { keyInput = it },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = colors.text),
                cursorBrush = SolidColor(colors.accent),
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Row(
            modifier = Modifier.padding(top = 13.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BriefButton(
                text = "Save",
                onClick = {
                    if (keyInput.isNotBlank()) {
                        onSetKey(keyInput)
                        keyInput = ""
                    }
                },
            )
            if (isConfigured) BriefTextButton(text = "Clear", onClick = onClearKey)
            BriefTextButton(text = "Get a key", onClick = onGetKey)
        }

        SourceSelector(isActive = isActive, isConfigured = isConfigured, onSelect = onSelect)
    }
}

@Composable
private fun SourceSelector(isActive: Boolean, isConfigured: Boolean, onSelect: () -> Unit) {
    val colors = BriefTheme.colors
    Row(
        modifier = Modifier
            .padding(top = 13.dp)
            .let { if (isConfigured) it.clickable(onClick = onSelect) else it },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .border(2.dp, if (isActive) colors.accent else colors.textTer, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (isActive) {
                Box(Modifier.size(8.dp).clip(CircleShape).background(colors.accent))
            }
        }
        Spacer(Modifier.size(11.dp))
        Text(
            text = if (isConfigured) "Use this source" else "Add a key to use this source",
            style = MaterialTheme.typography.bodyMedium,
            color = if (isConfigured) colors.text else colors.textSec,
        )
    }
}
