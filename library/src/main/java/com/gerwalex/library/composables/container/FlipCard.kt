package com.gerwalex.library.compose.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.lifecycle.Lifecycle
import com.gerwalex.library.composables.RepeatOnLifecycleEffect
import com.gerwalex.library.modifier.thenIf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

enum class CardFace(val angle: Float) {
    Front(0f) {
        override val next: CardFace
            get() = Back
    },
    Back(180f) {
        override val next: CardFace
            get() = Front
    };

    abstract val next: CardFace
}

enum class CardRotationAxis {
    AxisX,
    AxisY,
}

/**
 * A composable function that creates a flippable card with a front and back face.
 *
 * @param modifier The modifier to be applied to the card.
 * @param initialCardFace The initial face of the card (Front or Back). Defaults to Front.
 * @param onCardFaceChanged Callback function invoked when the card is clicked. Provides the current [CardFace].
 * @param rotationAxis The axis of rotation for the flip animation. Defaults to [CardRotationAxis.AxisY].
 * @param autoFlipDelayMillis The delay in milliseconds before the card automatically flips.
 *                      If 0 or negative, auto-flipping is disabled. Defaults to 0.
 * @param frontContent The composable content to be displayed on the front face of the card.
 * @param backContent The composable content to be displayed on the back face of the card. When null the card is not flippable.
 *
 * Example Usage:
 * ```
 * FlipCard(
 *     modifier = Modifier.size(100.dp),
 *     cardFace = CardFace.Front,
 *     onClick = {
 *         // Handle click event
 *         println("Card clicked: $it")
 *     },
 *     axis = RotationAxis.AxisY,
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
fun FlipCard(
    modifier: Modifier = Modifier,
    initialCardFace: CardFace = CardFace.Front,
    onCardFaceChanged: (CardFace) -> Unit = {},
    rotationAxis: CardRotationAxis = CardRotationAxis.AxisY,
    autoFlipDelayMillis: Long = 0,
    animationDurationMillis: Int = 400,
    backContent: @Composable (ColumnScope.() -> Unit)? = null,
    frontContent: @Composable ColumnScope.() -> Unit,

    ) {
    var myCardFace by remember(initialCardFace) { mutableStateOf(initialCardFace) }
    val onCardFaceChangedState by rememberUpdatedState(onCardFaceChanged)

    val rotation = animateFloatAsState(
        targetValue = myCardFace.angle,
        animationSpec = tween(
            durationMillis = animationDurationMillis,
            easing = FastOutSlowInEasing,
        )
    )
    Box(
        modifier = modifier
            .thenIf(backContent != null) {
                clickable {
                    onCardFaceChangedState(myCardFace)
                }
            }
            .graphicsLayer {
                if (rotationAxis == CardRotationAxis.AxisX) {
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
                Modifier.fillMaxWidth(),
            ) {
                frontContent()
            }
        } else {
            backContent?.let {
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
                Column(
                    Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            if (rotationAxis == CardRotationAxis.AxisX) {
                                rotationX = 180f
                            } else {
                                rotationY = 180f
                            }
                        },
                ) {
                    backContent()
                }
            }
        }
    }

}