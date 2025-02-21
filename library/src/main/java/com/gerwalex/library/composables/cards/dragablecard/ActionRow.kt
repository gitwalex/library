package com.gerwalex.library.composables.cards.dragablecard

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.gerwalex.library.composables.AppTheme

@Composable
fun ActionsRow(
    modifier: Modifier = Modifier,
    button1: @Composable () -> Unit,
    button2: (@Composable () -> Unit)? = null,
    button3: (@Composable () -> Unit)? = null,

    ) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        button1()
        if (button2 != null) {
            button2()
        }
        if (button3 != null) {
            button3()
        }

    }
}

@Composable
fun ActionsColumn(
    modifier: Modifier = Modifier,
    button1: @Composable () -> Unit = {},
    button2: @Composable () -> Unit = {},
    button3: @Composable () -> Unit = {},

    ) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        button1()
        button2()
        button3()

    }
}

@Preview(name = "Light", uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ActionRowPreview() {
    AppTheme {
        ActionsRow(
            button1 = {
                IconButton(
                    onClick = {},
                    content = {
                        Icon(
                            Icons.Default.Delete,
                            tint = Color.Gray,
                            contentDescription = "delete action",
                        )
                    }
                )
            },
            button2 = {
                IconButton(
                    onClick = {},
                    content = {
                        Icon(
                            Icons.Default.Delete,
                            tint = Color.Gray,
                            contentDescription = "delete action",
                        )
                    }
                )
            },
        )
    }
}

@Preview(name = "Light", uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ActionColumnPreview() {
    AppTheme {
        ActionsColumn(
            button1 = {
                IconButton(
                    onClick = {},
                    content = {
                        Icon(
                            Icons.Default.Delete,
                            tint = Color.Gray,
                            contentDescription = "delete action",
                        )
                    }
                )
            },
            button2 = {
                IconButton(
                    onClick = {},
                    content = {
                        Icon(
                            Icons.Default.Delete,
                            tint = Color.Gray,
                            contentDescription = "delete action",
                        )
                    }
                )
            },

            )
    }
}