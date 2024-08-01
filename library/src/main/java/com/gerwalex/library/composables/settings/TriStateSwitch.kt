package com.gerwalex.library.composables.settings

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TriStateToggle(
    states: List<String> = listOf("State 1", "State 2", "State 3"),
    selected: Int,
    selectedColor: Color = MaterialTheme.colorScheme.primaryContainer,
    unselectedColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
    onSelectionChange: (index: Int) -> Unit
) {

    Surface(
        shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.onPrimary),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            states.forEachIndexed { index, text ->
                Text(
                    text = text,
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(24.dp))
                        .clickable {
                            onSelectionChange(index)
                        }
                        .background(
                            if (index == selected)
                                selectedColor else unselectedColor

                        )
                        .padding(
                            vertical = 12.dp,
                            horizontal = 16.dp,
                        ),
                )
            }
        }
    }
}

@Preview(name = "Light", uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun TriStateTogglePreview() {
    Surface {
        TriStateToggle(selected = 0, onSelectionChange = {})
    }
}