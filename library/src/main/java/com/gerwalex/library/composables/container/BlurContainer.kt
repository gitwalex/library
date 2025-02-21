package com.gerwalex.library.composables.container


import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer

/**
 * A composable function that creates a container with a blurred background.
 *
 * This function overlays a blurred version of a provided component behind another composable content.
 * It leverages a custom blur effect provided by `customBlur` modifier. The blur effect is only
 * available on Android S (API level 31) and above.
 *
 * @param modifier The [Modifier] to be applied to the outer container.
 * @param blur The blur radius, which controls the intensity of the blur effect. Higher values
 *             result in a more intense blur. Default is `60f`.
 * @param component The composable function that will be used as the base for the blur effect.
 *                  This component will be rendered behind the `content` with the blur applied.
 * @param content The composable content that will be displayed on top of the blurred component.
 *                Default is an empty content.
 *
 * @throws IllegalStateException if the function is called on a device running Android version below S(API 31).
 *
 * Example usage:
 * ```
 * BlurContainer(
 *     modifier = Modifier.fillMaxSize(),
 *     blur = 30f,
 *     component = {
 *         Image(
 *             painter = painterResource(id = R.drawable.background),
 *             contentDescription = "Background Image",
 *             modifier = Modifier.fillMaxSize(),
 *             contentScale = ContentScale.Crop
 *         )
 *     },
 *     content = {
 *         Text(
 *             text = "Hello, Blurred World!",
 *             color = Color.White,
 *             modifier = Modifier.padding(16.dp)
 *         )
 *     }
 * )
 * ```
 */
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun BlurContainer(
    modifier: Modifier = Modifier,
    blur: Float = 60f,
    component: @Composable BoxScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit = {},
) {
    Box(modifier, contentAlignment = Alignment.Center) {

        Box(
            modifier = Modifier
                .customBlur(blur),
            content = component,
        )
        Box(
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

/**
 * Applies a custom blur effect to the composable.
 *
 * This modifier uses the `RenderEffect.createBlurEffect` to create a blur effect and applies it to the composable.
 * It only works on Android API level 31 (S) and above.
 *
 * @param blur The blur radius in pixels. A value of 0f means no blur. Larger values mean more blur.
 *             Must be a non-negative value.
 * @return A [Modifier] that applies the blur effect.
 *
 * @throws IllegalArgumentException If blur value is negative.
 *
 * @sample
 * ```
 * Box(modifier = Modifier
 *      .size(100.dp)
 *      .customBlur(10f) // Applies a blur with a radius of 10 pixels.
 *      .background(Color.Red)
 * )
 * ```
 */
@RequiresApi(Build.VERSION_CODES.S)
fun Modifier.customBlur(blur: Float) = this.then(
    graphicsLayer {
        if (blur > 0f)
            renderEffect = RenderEffect
                .createBlurEffect(
                    blur,
                    blur,
                    Shader.TileMode.DECAL,
                )
                .asComposeRenderEffect()
    }
)