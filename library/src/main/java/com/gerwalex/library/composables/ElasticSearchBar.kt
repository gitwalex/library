package com.gerwalex.library.compose.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ElasticSearchBarDemo(modifier: Modifier = Modifier) {
    ElasticSearchBar(
        modifier = modifier,
    )
}

/**
 * A search bar that expands and collapses with an elastic animation.
 *
 * @param modifier The modifier to be applied to the search bar.
 * @param textFieldDefaults The defaults to be used for the [TextField] when the search bar is expanded.
 */
@Composable
fun ElasticSearchBar(
    modifier: Modifier = Modifier,
    textFieldDefaults: TextFieldDefaults = TextFieldDefaults
) {
    var expanded by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    val width by animateDpAsState(
        targetValue = if (expanded) 300.dp else 48.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )

    Box(
        modifier = modifier
            .width(width)
            .height(48.dp)
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(24.dp)
            )
            .border(
                1.dp,
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                RoundedCornerShape(24.dp)
            )
    ) {
        if (!expanded) {
            IconButton(
                onClick = { expanded = true },
                modifier = Modifier.align(Alignment.Center)
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        } else {
            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Search...") },
                modifier = Modifier.fillMaxSize(),
                colors = textFieldDefaults.colors().copy(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                trailingIcon = {
                    IconButton(onClick = {
                        expanded = false
                        searchText = ""
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            )
        }
    }
}