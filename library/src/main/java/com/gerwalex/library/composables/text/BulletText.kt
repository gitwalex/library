package com.gerwalex.library.composables.text

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.gerwalex.library.composables.AppTheme
import com.gerwalex.library.ext.toSp

/**
 * Text mit Bullets.
 * Jede einzelne Zeile (Zeilenumbruch, /n) erhält einen Bullet,
 * Text darf selbst keine Bullets enthalten.
 */
@Composable
fun BulletText(
    text: String,
    modifier: Modifier = Modifier,
    bullet: String = "\u2022\u00A0\u00A0",
    highlightedText: String = "",
    highlightedTextColor: Color = MaterialTheme.colorScheme.primaryContainer,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    inlineContent: Map<String, InlineTextContent> = mapOf(),
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
) {
    val restLine = run {
        val textMeasurer = rememberTextMeasurer()
        remember(bullet, style, textMeasurer) {
            textMeasurer.measure(text = bullet, style = style).size.width
        }.toSp()
    }
    Text(
        text = remember(text, bullet, restLine, highlightedText, highlightedTextColor) {
            text.bulletWithHighlightedTxtAnnotatedString(
                bullet = bullet,
                restLine = restLine,
                highlightedTxt = highlightedText,
                highlightedTxtColor = highlightedTextColor,
            )
        },
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        inlineContent = inlineContent,
        onTextLayout = onTextLayout,
        style = style,
        modifier = modifier,
    )
}


fun String.bulletWithHighlightedTxtAnnotatedString(
    bullet: String,
    restLine: TextUnit,
    highlightedTxt: String,
    highlightedTxtColor: Color,
) = buildAnnotatedString {
    split("\n").forEach {
        var txt = it.trim()
        if (txt.isNotBlank()) {
            withStyle(style = ParagraphStyle(textIndent = TextIndent(restLine = restLine))) {
                append(bullet)
                if (highlightedTxt.isNotEmpty()) {
                    while (true) {
                        val i = txt.indexOf(string = highlightedTxt, ignoreCase = true)
                        if (i == -1) break
                        append(txt.subSequence(startIndex = 0, endIndex = i).toString())
                        val j = i + highlightedTxt.length
                        withStyle(style = SpanStyle(background = highlightedTxtColor)) {
                            append(txt.subSequence(startIndex = i, endIndex = j).toString())
                        }
                        txt = txt.subSequence(startIndex = j, endIndex = txt.length).toString()
                    }
                }
                append(txt)
            }
        }
    }
}

@Preview
@Composable
fun PreviewBulletText() {
    AppTheme {
        Surface {
            BulletText(
                text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit." +
                        "\nEtiam lipsums et metus vel mauris scelerisque molestie eget nec ligula." +
                        "\nNulla scelerisque, magna id aliquam rhoncus, ipsumx turpis risus sodales mi, sit ipsum amet malesuada nibh lacus sit amet libero." +
                        "\nCras in sem euismod, vulputate ligula in, egestas enim ipsum.",
                modifier = Modifier.padding(8.dp),
                highlightedText = "ipsum",
                overflow = TextOverflow.Ellipsis,
                onTextLayout = {},
            )
        }
    }
}