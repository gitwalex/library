package com.gerwalex.library.composables.settings

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.gerwalex.library.composables.AppTheme

@Composable
fun SettingSwitchItem(
    name: String,
    isChecked: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {

    Surface(
        color = Color.Transparent,
        modifier = modifier,
        onClick = { if (enabled) onClick() },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start,
            )
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                enabled = enabled,
                checked = isChecked,
                onCheckedChange = { onClick() }
            )
        }
    }
}

@Preview(name = "Light", uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SwitchTest() {
    AppTheme {
        Surface {
            SettingSwitchItem("Text", true) {}

        }
    }
}