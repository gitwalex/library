package com.gerwalex.haicard.ext.modifiers

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

@Composable
fun rememberShakeController(): ShakeController {
    return remember { ShakeController() }
}

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
 * @see <a href="https://www.sinasamaki.com/shake-animations-compose/">sinasamaki.com</a>
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