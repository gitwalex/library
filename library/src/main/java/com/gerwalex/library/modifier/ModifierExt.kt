package com.gerwalex.library.modifier

import android.graphics.Picture
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas

/**
 * Applies a modifier based on the non-nullity of a given value.
 *
 * This function provides a concise way to conditionally apply a modifier based on whether a value is null or not.
 * If the provided `value` is not null, the `builder` function is invoked with the non-null value, and the resulting
 * modifier is applied to the receiver. If the `value` is null, the receiver modifier is returned unchanged.
 *
 * @param value The value to check for nullity. If this value is not null, the `builder` function is invoked.
 * @param builder A function that takes the non-null `value` and returns a Modifier to be applied.
 * @return The modified Modifier, either with the result of the `builder` function applied or the original Modifier if `value` is null.
 *
 * @param T The type of the value being checked, which must be a non-nullable type.
 * @receiver Modifier The Modifier to which the conditional modifier will be applied.
 *
 * Example Usage:
 * ```
 * val myString: String? = "Hello"
 * val myModifier = Modifier
 *     .background(Color.Gray)
 *     .ifNotNull(myString) { str ->
 *         padding(16.dp).then(Modifier.clickable { println(str) })
 *     }
 *     .fillMaxWidth()
 * ```
 * In this example, if `myString` is not null, the padding and clickable modifiers will be applied.
 * Otherwise, only the background and fillMaxWidth modifiers will be applied.
 */
inline fun <T : Any> Modifier.ifNotNull(value: T?, builder: (T) -> Modifier): Modifier =
    value?.let { builder(value) } ?: this

/**
 * Applies a modifier based on a boolean condition.
 *
 * This extension function allows you to conditionally apply modifiers to a Composable element.
 * It checks the provided [condition] and applies either the [ifTrue] modifier or the [ifFalse] modifier
 * to the current `Modifier` instance.
 *
 * @param condition The boolean condition that determines which modifier to apply.
 * @param ifTrue A lambda function that returns a `Modifier` to apply if the [condition] is `true`.
 *               This lambda receives a fresh `Modifier` as its receiver (`Modifier.()`).
 * @param ifFalse An optional lambda function that returns a `Modifier` to apply if the [condition] is `false`.
 *                Defaults to `{ this }`, which means no additional modifier is applied if the condition is false.
 *                This lambda also receives a fresh `Modifier` as its receiver (`Modifier.()`).
 * @return A new `Modifier` instance with the conditionally applied modifier.
 *
 * **Example Usage:**
 * ```kotlin
 * Box(
 *     modifier = Modifier
 *         .fillMaxSize()
 *         .conditional(
 *             condition = isDarkMode,
 *             ifTrue = { background(Color.DarkGray).padding(16.dp) },
 *             ifFalse = { background(Color.LightGray) }
 *         )
 * ) {
 *     // ... content ...
 * }
 * ```
 * In this example if `isDarkMode` is true then the background will be dark gray and there will be 16dp of padding, otherwise only the background will be light gray.
 *
 * Another example where ifFalse is omitted:
 * ```kotlin
 * Text(
 *    "Hello World",
 *     modifier = Modifier
 *         .padding(8.dp)
 *         .conditional(
 *             condition = isBold,
 *             ifTrue = { fontStyle(FontStyle.Italic) },
 *         )
 * )
 * ```
 * In this example if `isBold` is true then the text will be italic, otherwise no extra modifier will be applied besides the 8dp of padding.
 */
inline fun Modifier.conditional(
    condition: Boolean,
    ifTrue: Modifier.() -> Modifier,
    ifFalse: Modifier.() -> Modifier = { this },
): Modifier = if (condition) {
    then(ifTrue(Modifier))
} else {
    then(ifFalse(Modifier))
}

/**
 * Erstellt ein 1:1 Picture des Composables.
 * @param picture Picture. Inhalt wird überschrieben
 */
@Composable
fun Modifier.takePictureOfComposable(picture: Picture) =
    drawWithCache {
        val width = this.size.width.toInt()
        val height = this.size.height.toInt()
        // Example that shows how to redirect rendering to an Android Picture and then
        // draw the picture into the original destination
        onDrawWithContent {
            val pictureCanvas = Canvas(
                picture.beginRecording(width, height)
            )
            draw(this, this.layoutDirection, pictureCanvas, this.size) {
                this@onDrawWithContent.drawContent()
            }
            picture.endRecording()
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawPicture(picture)
            }
        }
    }

fun Modifier.thenIf(condition: Boolean, modifier: Modifier.() -> Modifier) =
    if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }


/**
 * Scaling Animation onPress
 * see https://medium.com/@alohaabhi/beautiful-way-to-access-touch-interactions-in-jetpack-compose-c4b8444b5c95
 */
fun Modifier.scaleOnPress(
    interactionSource: InteractionSource
) = composed {
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        if (isPressed) {
            0.95f
        } else {
            1f
        }, label = "ScaleOnPress"
    )
    graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Creates a clickable modifier that doesn't show a ripple effect.
 *
 * This modifier is useful for situations where you want to detect clicks without the visual
 * feedback of a ripple.
 *
 * @param interactionSource [MutableInteractionSource] that will be used to track interactions with this
 * clickable modifier.
 * @param onClick The callback to be invoked when this clickable modifier is clicked.
 *
 * @return A [Modifier] that decorates the current modifier to make it clickable without ripple effect.
 */
fun Modifier.clickableWithoutRipple(
    interactionSource: MutableInteractionSource,
    onClick: () -> Unit
) = {
    this.then(
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = { onClick() }
        )
    )
}
