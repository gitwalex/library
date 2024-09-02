package com.gerwalex.library.composables.modifiers

import android.graphics.Picture
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas

inline fun <T : Any> Modifier.ifNotNull(value: T?, builder: (T) -> Modifier): Modifier =
    value?.let { builder(value) } ?: this

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
            val pictureCanvas = androidx.compose.ui.graphics.Canvas(
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
