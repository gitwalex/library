package com.gerwalex.library.modifier


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


/**
 * Applies a "jump" animation to the composable when clicked.
 *
 * This modifier uses a [JumpAnimationState] to manage the animation's progress.
 * When the composable is clicked, it will visually "jump" up and then return to its original position.
 *
 * The animation involves:
 * - Scaling down slightly on press.
 * - Translating upwards.
 * - Translating back down.
 * - A small "squish" effect upon returning to the original position.
 *
 * @param state The [JumpAnimationState] that controls the animation.
 *              This state should be created and remembered using [rememberJumpAnimationState].
 *
 * @see <a href="https://medium.com/flat-pack-tech/teaching-a-composable-to-jump-461456198af9"> Teaching a Composable to Jump</a>
 *
 */
fun Modifier.jumpOnClick(
    state: JumpAnimationState
) = this then Modifier
    .clickable(
        interactionSource = state.interactionSource,
        indication = null,
        onClick = { /* this is handled in the state */ },
    )
    .graphicsLayer {
        transformOrigin = TransformOrigin(
            pivotFractionX = 0.5f,
            pivotFractionY = 1.0f,
        )
        scaleY = state.scale.value
        translationY = state.translation.value * size.height
    }

/**
 * Remembers the state for the jump animation.
 *
 * @param onClick The callback to be invoked when the animation completes.
 * @param scope The coroutine scope to be used for the animation.
 * @param interactionSource The interaction source to be used for the animation.
 * @return The state for the jump animation.
 */
@Composable
fun rememberJumpAnimationState(
    onClick: () -> Unit,
    scope: CoroutineScope = rememberCoroutineScope(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
): JumpAnimationState {
    val state = remember(scope, interactionSource) {
        JumpAnimationState(
            scope = scope,
            interactionSource = interactionSource,
        )
    }

    val onClickLambda by rememberUpdatedState(onClick)
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> state.onPress()
                is PressInteraction.Release -> state.onRelease(onClickLambda)
                is PressInteraction.Cancel -> state.onCancel()
            }
        }
    }

    return state
}

/**
 * State for managing the jump animation.
 *
 * This class handles the animation logic for the jump effect, including scaling and translation
 * of the composable. It uses [Animatable] for smooth animations and manages the animation lifecycle
 * using a [CoroutineScope].
 *
 * @property interactionSource The [MutableInteractionSource] to observe press, release, and cancel interactions.
 * @property scope The [CoroutineScope] to launch and manage animations.
 */
@Stable
class JumpAnimationState(
    val interactionSource: MutableInteractionSource,
    val scope: CoroutineScope,
) {
    private var animation: Job? = null

    val scale = Animatable(initialValue = 1f)
    val translation = Animatable(initialValue = 0f)

    fun onPress() = launchAnimation {
        scale.snapTo(defaultScale)
        translation.snapTo(defaultTranslation)

        scale.animateTo(pressedScale, defaultSpring)
    }

    fun onRelease(invokeOnCompletion: () -> Unit) = launchAnimation {
        // ensure it's fully compressed if the user just quickly tapped
        scale.animateTo(pressedScale, defaultSpring)

        launch {
            // restore the scale
            scale.animateTo(defaultScale, releaseScaleSpring)
        }

        launch {
            // up like a sun...
            translation.animateTo(launchTranslation, launchTranslationSpring)

            // ...down like a pancake
            var isSquishing = false
            translation.animateTo(defaultTranslation, returnTranslationSpring) {
                val hitTheGround = value >= defaultTranslation
                if (hitTheGround && !isSquishing) {
                    isSquishing = true
                    invokeOnCompletion()
                    // Add a small squish on impact
                    launch {
                        scale.animateTo(squishScale, defaultSpring)
                        scale.animateTo(defaultScale, defaultSpring)
                    }
                }
            }
        }
    }

    fun onCancel() = launchAnimation {
        scale.snapTo(defaultScale)
        translation.snapTo(defaultTranslation)
    }

    private fun launchAnimation(block: suspend CoroutineScope.() -> Unit) {
        animation?.cancel()
        animation = scope.launch(block = block)
    }
}

private const val defaultScale = 1f
private const val pressedScale = 0.6f
private const val squishScale = 0.88f
private const val defaultTranslation = 0f
private const val launchTranslation = -0.8f
private val defaultSpring = spring<Float>()
private val releaseScaleSpring = spring<Float>(
    stiffness = Spring.StiffnessMedium,
    dampingRatio = 0.55f
)
private val launchTranslationSpring = spring<Float>(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = Spring.StiffnessMediumLow,
)
private val returnTranslationSpring = spring<Float>(
    dampingRatio = 0.65f,
    stiffness = 140f,
)