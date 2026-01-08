package com.gerwalex.library.animation

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.DurationUnit

/**
 * A Composable function that displays a heartbeat-like ripple animation around a central content.
 * It also supports an exit animation where a circle expands to fill the screen.
 *
 * The ripple animation consists of multiple expanding and fading circles.
 * The exit animation is triggered when `isVisible` becomes `false`.
 *
 * @param modifier Modifier to be applied to the root Box of the animation.
 * @param isVisible Controls the visibility of the heartbeat animation.
 *                  When set to `false`, the exit animation will start.
 * @param exitAnimationDuration The duration of the exit animation.
 *                              Defaults to [Duration.ZERO], meaning no exit animation by default.
 * @param exitAnimationCircleColor The color of the expanding circle during the exit animation.
 *                                 Defaults to [Color.Blue].
 * @param onStartExitAnimation A lambda that is invoked when the exit animation starts.
 *                             Defaults to an empty lambda.
 * @param content The Composable content to be displayed at the center of the animation.
 *                This content will be surrounded by the ripple effect.
 */
@Composable
fun HeartBeatAnimation(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    exitAnimationDuration: Duration = Duration.ZERO,
    exitAnimationCircleColor: Color = Color.Blue,
    onStartExitAnimation: () -> Unit = {},
    content: @Composable () -> Unit
) {
    // Animation constants
    val rippleCount = 4
    val rippleDurationMs = 3313
    val rippleDelayMs = rippleDurationMs / 8
    val baseSize = 144.dp
    val containerSize = 288.dp

    // Track exit animation state
    var isExitAnimationStarted by remember { mutableStateOf(false) }

    // Trigger exit animation when visibility changes
    LaunchedEffect(isVisible) {
        if (!isVisible && !isExitAnimationStarted) {
            isExitAnimationStarted = true
            onStartExitAnimation()
        }
    }

    // Calculate screen diagonal for exit animation scaling
    val configuration = LocalWindowInfo.current.containerSize
    val screenWidth = configuration.width
    val screenHeight = configuration.height
    val screenDiagonal = sqrt((screenWidth * screenWidth + screenHeight * screenHeight).toFloat())

    // Exit animation scale with snappy easing
    val snappyEasing = CubicBezierEasing(0.2f, 0.0f, 0.2f, 1.0f)
    val exitAnimationScale by animateFloatAsState(
        targetValue = if (isExitAnimationStarted) screenDiagonal / baseSize.value else 0f,
        animationSpec = tween(
            durationMillis = exitAnimationDuration.toInt(DurationUnit.MILLISECONDS),
            easing = snappyEasing
        ),
        label = "exitScale"
    )

    // Infinite ripple animation transition
    val infiniteTransition = rememberInfiniteTransition(label = "heartbeatTransition")

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Only show ripples when visible and not exiting
        if (isVisible && !isExitAnimationStarted) {
            Box(
                modifier = Modifier.size(containerSize),
                contentAlignment = Alignment.Center
            ) {
                // Create ripple circles with staggered animations
                repeat(rippleCount) { index ->
                    RippleCircle(
                        infiniteTransition = infiniteTransition,
                        index = index,
                        rippleDurationMs = rippleDurationMs,
                        rippleDelayMs = rippleDelayMs,
                        baseSize = baseSize,
                        content = content,
                    )
                }
            }
        }

        // Exit animation circle
        if (isExitAnimationStarted) {
            Box(
                modifier = Modifier
                    .size(baseSize)
                    .graphicsLayer {
                        scaleX = exitAnimationScale
                        scaleY = exitAnimationScale
                    }
                    .background(
                        color = exitAnimationCircleColor,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
private fun RippleCircle(
    infiniteTransition: InfiniteTransition,
    index: Int,
    rippleDurationMs: Int,
    rippleDelayMs: Int,
    baseSize: Dp,
    content: @Composable () -> Unit
) {
    val totalDuration = rippleDurationMs + (rippleDelayMs * index)
    val easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)

    // Animate scale from 1f to 4f
    val animatedScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = totalDuration,
                delayMillis = rippleDelayMs * index,
                easing = easing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rippleScale$index"
    )

    // Animate alpha from 0.25f to 0f
    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = totalDuration,
                delayMillis = rippleDelayMs * index,
                easing = easing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rippleAlpha$index"
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(baseSize)
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
                alpha = animatedAlpha
            }
    ) {
        content()
    }
}


