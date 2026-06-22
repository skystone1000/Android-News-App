package com.example.newsapp.presentation.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.newsapp.domain.model.SourceMetadata
import com.example.newsapp.domain.usage.UsageSnapshot

/**
 * One provider row: name + status, a usage meter (when configured), a masked key field
 * (save / clear), a "Get a key" link, and an active-source selector enabled only once a key exists.
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
    modifier: Modifier = Modifier
) {
    var keyInput by rememberSaveable(metadata.id) { mutableStateOf("") }

    OutlinedCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = metadata.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = when {
                        isActive -> "Active"
                        isConfigured -> "Ready"
                        else -> "No key"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isConfigured && usage != null) {
                UsageMeter(snapshot = usage, modifier = Modifier.padding(top = 8.dp))
            }

            OutlinedTextField(
                value = keyInput,
                onValueChange = { keyInput = it },
                label = { Text("API key (${metadata.keyParamHint})") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        onSetKey(keyInput)
                        keyInput = ""
                    },
                    enabled = keyInput.isNotBlank()
                ) { Text("Save") }

                if (isConfigured) {
                    TextButton(onClick = onClearKey) { Text("Clear") }
                }
                TextButton(onClick = onGetKey) { Text("Get a key") }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isActive,
                    onClick = onSelect,
                    enabled = isConfigured
                )
                Text(
                    text = if (isConfigured) "Use this source" else "Add a key to use this source",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isConfigured) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}
