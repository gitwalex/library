package com.gerwalex.library.compose.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import com.gerwalex.library.composables.RepeatOnLifecycleEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

enum class BoxFace(val angle: Float) {
    Front(0f) {
        override val next: BoxFace
            get() = Back
    },
    Back(180f) {
        override val next: BoxFace
            get() = Front
    };

    abstract val next: BoxFace
}

enum class BoxRotationAxis {
    AxisX,
    AxisY,
}

/**
 * A composable function that creates a flippable box with a front and back face.
 *
 * @param modifier The modifier to be applied to the card.
 * @param initialBoxFace The initial face of the box (Front or Back). Defaults to Front.
 * @param onBoxFaceChanged Callback function invoked when the box is clicked. Provides the current [BoxFace].
 * @param rotationAxis The axis of rotation for the flip animation. Defaults to [BoxRotationAxis.AxisY].
 * @param autoFlipDelayMillis The delay in milliseconds before the box automatically flips.
 *                      If 0 or negative, auto-flipping is disabled. Defaults to 0.
 * @param frontContent The composable content to be displayed on the front face of the box.
 * @param backContent The composable content to be displayed on the back face of the box.
 *
 * Example Usage:
 * ```
 * FlipBox(
 *     modifier = Modifier.size(100.dp),
 *     BoxFace = BoxFace.Front,
 *     onClick = {
 *         // Handle click event
 *         println("Box clicked: $it")
 *     },
 *     axis = BoxRotationAxis.AxisY,
 *     autoFlipDelay = 3000, // Auto-flip every 3 seconds
 *     front = {
 *         Text("Front")
 *     },
 *     back = {
 *         Text("Back")
 *     }
 * )
 */
@Composable
fun FlipBox(
    modifier: Modifier = Modifier,
    initialBoxFace: BoxFace = BoxFace.Front,
    onBoxFaceChanged: (BoxFace) -> Unit = {},
    rotationAxis: BoxRotationAxis = BoxRotationAxis.AxisY,
    autoFlipDelayMillis: Long = 0,
    animationDurationMillis: Int = 400,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalArrangement: Arrangement.HorizontalOrVertical = Arrangement.spacedBy(16.dp),
    backContent: @Composable ColumnScope.() -> Unit = {},
    frontContent: @Composable ColumnScope.() -> Unit,

    ) {
    var myCardFace by remember(initialBoxFace) { mutableStateOf(initialBoxFace) }
    val onBoxFaceChangedState by rememberUpdatedState(onBoxFaceChanged)
    val rotation = animateFloatAsState(
        targetValue = myCardFace.angle,
        animationSpec = tween(
            durationMillis = animationDurationMillis,
            easing = FastOutSlowInEasing,
        )
    )
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable {
                onBoxFaceChangedState(myCardFace)
            }
//            .verticalScroll(rememberScrollState())
            .graphicsLayer {
                if (rotationAxis == BoxRotationAxis.AxisX) {
                    rotationX = rotation.value
                } else {
                    rotationY = rotation.value
                }
                cameraDistance = 12f * density
            },
        contentAlignment = Alignment.Center
    ) {
        if (rotation.value <= 90f) {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = horizontalAlignment,
                verticalArrangement = verticalArrangement,
            ) {
                frontContent()
            }
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        if (rotationAxis == BoxRotationAxis.AxisX) {
                            rotationX = 180f
                        } else {
                            rotationY = 180f
                        }
                    },
                horizontalAlignment = horizontalAlignment,
                verticalArrangement = verticalArrangement,
            ) {
                backContent()
            }
        }
    }
    RepeatOnLifecycleEffect(state = Lifecycle.State.STARTED) {
        if (autoFlipDelayMillis > 0) {
            coroutineScope {
                while (isActive) {
                    withContext(Dispatchers.IO) { delay(autoFlipDelayMillis) }
                    myCardFace = myCardFace.next
                }
            }
        }
    }

}