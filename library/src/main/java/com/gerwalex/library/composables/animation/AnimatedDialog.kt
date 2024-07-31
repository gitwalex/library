package com.gerwalex.library.composables.animation

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AnimatedDialog(
    onDismiss: () -> Unit,
    inAnimDuration: Int = 720,
    outAnimDuration: Int = 450,
    properties: DialogProperties = DialogProperties(),
    content: @Composable (triggerDismiss: () -> Unit) -> Unit,
) {
    val scope = rememberCoroutineScope()
    // A state to manage the animations
    var isDialogVisible by remember { mutableStateOf(false) }
    // A common animation spec which will be used among different animations
    val animationSpec = tween<Float>(
        if (isDialogVisible) inAnimDuration else outAnimDuration
    )
    val dialogAlpha by animateFloatAsState(
        targetValue = if (isDialogVisible) 1f else 0f,
        animationSpec = animationSpec
    )

    val dialogRotationX by animateFloatAsState(
        targetValue = if (isDialogVisible) 0f else -90f,
        animationSpec = animationSpec
    )

    val dialogScale by animateFloatAsState(
        targetValue = if (isDialogVisible) 1f else 0f,
        animationSpec = animationSpec
    )
    val dismissWithAnimation: () -> Unit = {
        scope.launch {
            // Trigger the exit animation
            isDialogVisible = false
            // Wait for completion
            delay(outAnimDuration.toLong())
            // Trigger dialog dismiss
            onDismiss()
        }
    }
    LaunchedEffect(Unit) {
        isDialogVisible = true
    }
    Dialog(
        onDismissRequest = dismissWithAnimation,
        properties = properties
    ) {
        Box(
            modifier = Modifier
                // Apply alpha transition
                .alpha(dialogAlpha)
                // Apply scale transition
                .scale(dialogScale)
                // Apply rotation x transition
                .graphicsLayer { rotationX = dialogRotationX },
            content = {
                content(dismissWithAnimation)
            }
        )
    }
}

@Preview(name = "Light", uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun AnimatedDialogPreview() {
    MaterialTheme {
        Surface {
            AnimatedDialog(onDismiss = { /*TODO*/ }) {
                Text("DialogText")
            }
        }
    }
}