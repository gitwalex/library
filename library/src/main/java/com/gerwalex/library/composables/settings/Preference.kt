package com.gerwalex.library.composables.settings

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gerwalex.library.composables.AppTheme


@Composable
fun Preference(
    title: String,
    modifier: Modifier = Modifier,
    titleStyle: TextStyle = MaterialTheme.typography.titleMedium,
    description: String? = null,
    descriptionStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    onClick: () -> Unit = { },
    enabled: Boolean = true,
) {
    Column(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.8f)
            .clickable {
                onClick()
            },
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = title,
            style = titleStyle,
            maxLines = 1,
            modifier = Modifier.alpha(if (enabled) 1f else 0.8f)
        )
        description?.let {
            Text(
                text = description,
                style = descriptionStyle,
                modifier = Modifier.alpha(if (enabled) 1f else 0.8f)
            )
        }
    }
}


@Preview(name = "Light", uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreferenceEnabled() {
    AppTheme {
        Surface {
            Preference(
                title = "Preference Light Text",
                description = "Preference Light Description"
            )
        }
    }
}

@Preview(name = "Light", uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreferenceDisabled() {
    AppTheme {
        Surface {
            Preference(
                title = "Preference Light Text",
                description = "Preference Light Description",
                enabled = false
            )
        }
    }
}
