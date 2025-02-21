package com.gerwalex.library.composables.snackbar

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *  @see <a href="https://medium.com/@kappdev/how-to-create-a-countdown-snackbar-in-android-with-jetpack-compose-d58bcd8011cf"> Countdown Snackbar</a>
 * A customizable Snackbar with a countdown timer.
 *
 * This composable displays a Snackbar with a built-in countdown timer. The timer is visualized as a progress indicator
 * and displays the remaining seconds. It automatically dismisses the Snackbar when the timer reaches zero.
 *
 * @param snackbarData The data associated with the Snackbar, including the message, action, and dismiss action.
 * @param modifier Modifier for styling and layout of the Snackbar.
 * @param durationInSeconds The duration of the countdown timer in seconds. Defaults to 5 seconds.
 */
@Composable
fun CountdownSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
    durationInSeconds: Int = 5,
    actionOnNewLine: Boolean = false,
    shape: Shape = SnackbarDefaults.shape,
    containerColor: Color = SnackbarDefaults.color,
    contentColor: Color = SnackbarDefaults.contentColor,
    actionColor: Color = SnackbarDefaults.actionColor,
    actionContentColor: Color = SnackbarDefaults.actionContentColor,
    dismissActionContentColor: Color = SnackbarDefaults.dismissActionContentColor,
) {
    val totalDuration = remember(durationInSeconds) { durationInSeconds * 1000 }
    var millisRemaining by remember { mutableIntStateOf(totalDuration) }

    LaunchedEffect(snackbarData) {
        while (millisRemaining > 0) {
            delay(40)
            millisRemaining -= 40
        }
        snackbarData.dismiss()
    }
    // Define the action button if an action label is provided
    val actionLabel = snackbarData.visuals.actionLabel
    val actionComposable: (@Composable () -> Unit)? = if (actionLabel != null) {
        @Composable {
            TextButton(
                colors = ButtonDefaults.textButtonColors(contentColor = actionColor),
                onClick = { snackbarData.performAction() },
                content = { Text(actionLabel) }
            )
        }
    } else {
        null
    }

    // Define the dismiss button if the snackbar includes a dismiss action
    val dismissActionComposable: (@Composable () -> Unit)? =
        if (snackbarData.visuals.withDismissAction) {
            @Composable {
                IconButton(
                    onClick = { snackbarData.dismiss() },
                    content = {
                        Icon(Icons.Rounded.Close, null)
                    }
                )
            }
        } else {
            null
        }
    Snackbar(
        modifier = modifier.padding(12.dp), // Apply padding around the snackbar
        action = actionComposable,
        actionOnNewLine = actionOnNewLine,
        dismissAction = dismissActionComposable,
        dismissActionContentColor = dismissActionContentColor,
        actionContentColor = actionContentColor,
        containerColor = containerColor,
        contentColor = contentColor,
        shape = shape,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SnackbarCountdown(
                // Calculate the progress of the timer
                timerProgress = millisRemaining.toFloat() / totalDuration.toFloat(),
                // Calculate the remaining seconds
                secondsRemaining = (millisRemaining / 1000) + 1,
                color = contentColor
            )
            // Display the message
            Text(snackbarData.visuals.message)
        }
    }
}

@Composable
private fun SnackbarCountdown(
    timerProgress: Float,
    secondsRemaining: Int,
    color: Color
) {
    Box(
        modifier = Modifier.size(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.matchParentSize()) {
            // Define the stroke
            val strokeStyle = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
            // Draw the track
            drawCircle(
                color = color.copy(alpha = 0.12f),
                style = strokeStyle
            )
            // Draw the progress
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = (-360f * timerProgress),
                useCenter = false,
                style = strokeStyle
            )
        }
        // Display the remaining seconds
        Text(
            text = secondsRemaining.toString(),
            style = LocalTextStyle.current.copy(
                fontSize = 14.sp,
                color = color
            )
        )
    }
}

@Preview(name = "Light", uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun CountDownSnackBarPreview() {
    Surface {
        Box(Modifier.fillMaxSize()) {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            // Define a SnackbarHostState to manage the state of the snackbar
            val snackbarHostState = remember { SnackbarHostState() }

            Button(
                modifier = Modifier.align(Alignment.Center),
                onClick = {
                    scope.launch {
                        // Show a snackbar
                        val result = snackbarHostState.showSnackbar(
                            message = "User account deleted.",
                            actionLabel = "UNDO",
                            duration = SnackbarDuration.Indefinite
                        )
                        // Handle the snackbar result
                        when (result) {
                            SnackbarResult.Dismissed -> {
                                Toast.makeText(context, "Deleted permanently", Toast.LENGTH_SHORT)
                                    .show()
                            }

                            SnackbarResult.ActionPerformed -> {
                                Toast.makeText(context, "Deletion canceled", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }
            ) {
                Text("Delete Account")
            }

            // Create a SnackbarHost to display the snackbar
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(BottomCenter)
            ) { data ->
                // Use the CountdownSnackbar
                CountdownSnackbar(data)
            }
        }
    }
}