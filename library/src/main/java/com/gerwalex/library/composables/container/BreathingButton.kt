package com.gerwalex.library.compose.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun BreathingButtonDemo(modifier: Modifier = Modifier) {
    var isLoading by remember { mutableStateOf(false) }
    LaunchedEffect(isLoading) {
        if (isLoading) {
            delay(5.seconds)
            isLoading = false
        }
    }
    BreathingButton(isLoading = isLoading, modifier = modifier, onClick = { isLoading = true })
}

/**
 * A Composable function that creates a button with a "breathing" animation when in a loading state.
 *
 * When `isLoading` is true, the button will display a [CircularProgressIndicator] and will have a
 * subtle scaling animation, giving it a "breathing" effect. The button will also be disabled.
 * When `isLoading` is false, the button will display the provided [text] and will be enabled.
 *
 * @param isLoading A boolean indicating whether the button is in a loading state. Defaults to `false`.
 * @param text The text to display on the button when it's not loading. Defaults to "Submit".
 * @param modifier The [Modifier] to be applied to the button. Defaults to [Modifier].
 * @param onClick A lambda function to be executed when the button is clicked.
 */
@Composable
fun BreathingButton(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    text: String = "Submit",
    onClick: () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isLoading) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        )
    )

    Button(
        onClick = onClick,
        modifier = modifier.scale(scale),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(text)
        }
    }
}