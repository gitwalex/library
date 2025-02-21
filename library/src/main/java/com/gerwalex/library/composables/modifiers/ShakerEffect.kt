package com.gerwalex.library.composables.modifiers

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.roundToInt


/**
 * Configuration class for the shake animation.
 *
 * This class defines the parameters that control the behavior of the shake effect,
 * such as the number of iterations, intensity, and various transformations like rotation,
 * scaling, and translation.
 *
 * @property iterations The number of times the shake effect should repeat.
 *                      A higher value will result in a longer shake.
 * @property intensity The overall strength of the shake. This affects the magnitude
 *                     of the applied transformations. A higher value means a more
 *                     pronounced shake. Defaults to 100_000f.
 * @property rotate The amount of rotation to apply during the shake (in degrees).
 *                  Positive values rotate clockwise, negative values counter-clockwise. Defaults to 0f.
 * @property rotateX The amount of rotation around the X-axis to apply during the shake (in degrees).
 *                   Positive values rotate downwards, negative values upwards. Defaults to 0f.
 * @property rotateY The amount of rotation around the Y-axis to apply during the shake (in degrees).
 *                   Positive values rotate to the right, negative values to the left. Defaults to 0f.
 * @property scaleX The amount to scale the X-axis during the shake.
 *                  Values greater than 0 will increase the horizontal size, while values less than 0
 *                  will decrease it. Defaults to 0f (no scaling).
 * @property scaleY The amount to scale the Y-axis during the shake.
 *                  Values greater than 0 will increase the vertical size, while values less than 0
 *                  will decrease it. Defaults to 0f (no scaling).
 * @property translateX The amount to translate (move) along the X-axis during the shake.
 *                     Positive values move right, negative values move left. Defaults to 0f (no translation).
 * @property translateY The amount to translate (move) along the Y-axis during the shake.
 *                     Positive values move down, negative values move up. Defaults to 0f (no translation).
 * @property delay The delay (in milliseconds) before the shake animation starts. Defaults to 0.
 */
class ShakeConfig(
    val iterations: Int,
    val intensity: Float = 100_000f,
    val rotate: Float = 0f,
    val rotateX: Float = 0f,
    val rotateY: Float = 0f,
    val scaleX: Float = 0f,
    val scaleY: Float = 0f,
    val translateX: Float = 0f,
    val translateY: Float = 0f,
    val delay: Long = 0,
)

/**
 * Creates and remembers a [ShakeController] instance across recompositions.
 *
 * This function provides a way to access and manage shake detection events within a Composable.
 * It uses [remember] to ensure that the [ShakeController] is only created once and is reused
 * on subsequent recompositions, preserving its internal state.
 *
 * Example usage:
 * ```
 * val shakeController = rememberShakeController()
 *
 * // Start listening for shake events
 * LaunchedEffect(Unit) {
 *     shakeController.start()
 * }
 *
 * // Stop listening when no longer needed
 * DisposableEffect(Unit) {
 *      onDispose {
 *          shakeController.stop()
 *      }
 * }
 *
 * // Observe the shake event flow
 * LaunchedEffect(shakeController) {
 *     shakeController.shakeEvent.collect {
 *         // Handle shake event here
 *         println("Device shaken!")
 *     }
 * }
 * ```
 *
 * @return A [ShakeController] instance that is remembered across recompositions.
 */
@Composable
fun rememberShakeController(): ShakeController {
    return remember { ShakeController() }
}

/**
 * `ShakeController` is responsible for managing and triggering shake effects.
 *
 * It holds a `ShakeConfig` which defines the properties of the shake, such as
 * duration, intensity, and offset. The `shake()` function is used to initiate
 * a new shake effect by providing a `ShakeConfig`.
 *
 * The current `ShakeConfig` is exposed as a mutable state, allowing Compose
 * UI elements to react to changes and animate accordingly.
 *
 * @property shakeConfig The current [ShakeConfig] being applied. It's a mutable
 *                       state that UI elements can observe to react to shake changes.
 *                       Initially set to `null` meaning no shake is active. It is
 *                       private set to ensure modification is only done via the `shake` method.
 */
class ShakeController {
    var shakeConfig: ShakeConfig? by mutableStateOf(null)
        private set

    fun shake(shakeConfig: ShakeConfig) {
        Log.d("ShakerEffect", "shakeconfig old=${this.shakeConfig}, new=$shakeConfig")
        this.shakeConfig = shakeConfig
    }
}

/**
 * Shakes a Composable
 * @see <a href="https://www.sinasamaki.com/shakeanimationscompose/">sinasamaki.com</a>
 * Applies a shaking animation to a Composable.
 *
 * This modifier allows you to apply a configurable shake animation to any Composable.
 * The animation can be customized in terms of its intensity, duration, rotation, scaling, and translation.
 * The animation is triggered and controlled by a [ShakeController].
 *
 * @param shakeController The controller that manages the shake animation. It holds the configuration for the shake
 *                        and triggers the animation when its `shakeConfig` is updated. If `shakeConfig` is null no
 *                        animation is done.
 *
 * @see ShakeController
 * @see ShakeConfig
 * @see <a href="https://www.sinasamaki.com/shakeanimationscompose/">Shake Animations in Jetpack Compose (sinasamaki.com)</a> for a detailed explanation and visual examples.
 *
 * @sample ExampleUsage  // Add a sample call to the shake modifier
 */
fun Modifier.shake(shakeController: ShakeController) = composed {
    val shake = remember { Animatable(0f) }
    shakeController.shakeConfig?.let { shakeConfig ->
        LaunchedEffect(shakeController.shakeConfig) {
            delay(shakeConfig.delay)
            for (i in 0..shakeConfig.iterations) {
                when (i % 2) {
                    0 -> shake.animateTo(1f, spring(stiffness = shakeConfig.intensity))
                    else -> shake.animateTo(-1f, spring(stiffness = shakeConfig.intensity))
                }
            }
            shake.animateTo(0f)
        }

        this
            .rotate(shake.value * shakeConfig.rotate)
            .graphicsLayer {
                rotationX = shake.value * shakeConfig.rotateX
                rotationY = shake.value * shakeConfig.rotateY
            }
            .scale(
                scaleX = 1f + (shake.value * shakeConfig.scaleX),
                scaleY = 1f + (shake.value * shakeConfig.scaleY),
            )
            .offset {
                IntOffset(
                    (shake.value * shakeConfig.translateX).roundToInt(),
                    (shake.value * shakeConfig.translateY).roundToInt(),
                )
            }
    } ?: this
}

@PreviewLightDark
@Composable
fun ShakerButton() {
    val shakeController = rememberShakeController()
    Box(
        modifier = Modifier
            .clickable {
                shakeController.shake(ShakeConfig(10, translateX = 5f))
            }
            .shake(shakeController)
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Surface {
            Text(text = "Shake me")
        }
    }
}

@PreviewLightDark
@Composable
fun ShakerTryAgain() {
    val shakeController = rememberShakeController()
    val config = ShakeConfig(
        iterations = 4,
        intensity = 2_000f,
        rotateY = 15f,
        translateX = 40f,
    )

    Box(
        modifier = Modifier
            .clickable {
                shakeController.shake(config)
            }
            .shake(shakeController)
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Surface {
            Text(text = "Try again")
        }
    }
}

@PreviewLightDark
@Composable
fun ShakerSuccess() {
    val shakeController = rememberShakeController()
    val config = ShakeConfig(
        iterations = 4,
        intensity = 1_000f,
        rotateX = -20f,
        translateY = 20f,
    )
    Box(
        modifier = Modifier
            .clickable {
                shakeController.shake(config)
            }
            .shake(shakeController)
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Surface {
            Text(text = "Success")
        }
    }
}