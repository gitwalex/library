package com.gerwalex.library.composables.container

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import org.intellij.lang.annotations.Language

@Language("AGSL")
const val ShaderSource = """
    uniform shader composable;
    
    uniform float visibility;
    
    half4 main(float2 cord) {
        half4 color = composable.eval(cord);
        color.a = step(visibility, color.a);
        return color;
    }
"""

/**
 * A composable container that applies a runtime shader effect to its content.
 *
 * This function creates a [Box] composable and applies a [RenderEffect] based on a [RuntimeShader]
 * to its content. The shader effect modifies the appearance of the composable's children.
 *
 * @param modifier The [Modifier] to be applied to the [Box] composable. This allows for
 *                 customizing the layout and appearance of the container.
 * @param content The composable content to be rendered within the [Box]. The content will
 *                be affected by the applied shader.
 *
 * @throws IllegalStateException If called on a platform before Android Tiramisu (API level 33).
 *
 * @sample
 * ```kotlin
 *  @Composable
 *  fun MyShaderExample() {
 *      ShaderContainer(Modifier.fillMaxSize()) {
 *          Text("Hello with Shader!", color = Color.White, modifier = Modifier.align(Alignment.Center))
 *      }
 *  }
 * ```
 *
 * @see RenderEffect
 * @see RuntimeShader
 * @see Modifier
 * @see Box
 * @see graphicsLayer
 * @see asComposeRenderEffect
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ShaderContainer(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val runtimeShader = remember {
        RuntimeShader(ShaderSource)
    }
    Box(
        modifier
            .graphicsLayer {
                runtimeShader.setFloatUniform("visibility", 0.2f)
                renderEffect = RenderEffect
                    .createRuntimeShaderEffect(
                        runtimeShader, "composable"
                    )
                    .asComposeRenderEffect()
            }
    ) {
        content()
    }
}