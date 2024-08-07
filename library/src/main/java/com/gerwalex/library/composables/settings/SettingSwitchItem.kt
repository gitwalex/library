package com.gerwalex.library.composables.settings

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun SettingSwitchItem(
    title: String,
    checked: Boolean,
    onCheckedChange: suspend (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    titleStyle: TextStyle = MaterialTheme.typography.titleLarge,
    description: String? = null,
    descriptionStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    enabled: Boolean = true,
) {
    val scope = rememberCoroutineScope()
    Row(
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .fillMaxWidth()
            .toggleable(
                value = checked,
                enabled = enabled,
                role = Role.Switch,
                onValueChange = {
                    scope.launch {
                        onCheckedChange(it)
                    }
                }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Preference(
            modifier = Modifier.weight(1.0f),
            title = title,
            titleStyle = titleStyle,
            description = description,
            descriptionStyle = descriptionStyle,
        )
        Switch(
            checked = checked,
            onCheckedChange = null,
            enabled = enabled,
            colors = SwitchDefaults.colors()


        )
    }
}

