package com.gerwalex.library.composables.text

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.gerwalex.library.ext.dpToIntPx
import kotlin.math.max

data class AutoCompleteTextViewState<T>(
    val queryLabel: String,
    val initialQuery: String,
) {
    var query by mutableStateOf(initialQuery)
    var list: List<T> by mutableStateOf(emptyList())
    var errorText: String? by mutableStateOf(null)
    var textLines: Int by mutableStateOf(5)

}

@Composable
fun <T> rememberAutoCompleteTextViewState(
    initialQuery: String,
    queryLabel: String,
    list: List<T>,
    textLines: Int = 5,
    errorText: String? = null,
): AutoCompleteTextViewState<T> {
    return remember {
        AutoCompleteTextViewState<T>(
            initialQuery = initialQuery,
            queryLabel = queryLabel
        ).also {
            it.list = list
            it.textLines = textLines
            it.errorText = errorText
        }
    }
}

@Composable
fun <T> AutoCompleteTextView(
    state: AutoCompleteTextViewState<T>,
    modifier: Modifier = Modifier,
    onQueryChanged: (query: String) -> Unit = {},
    onDismissRequest: () -> Unit = {},
    onItemClick: (T) -> Unit = {},
    onFocusChanged: (isFocused: Boolean) -> Unit = {},
    itemContent: @Composable (T) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    var shouldShowDropdown by remember { mutableStateOf(false) }
    val fieldHeight = LocalDensity.current.dpToIntPx(56.dp)
    var fieldWidth by remember { mutableStateOf(0.dp) }
    DisposableEffect(key1 = Unit) {
        onDispose {
            shouldShowDropdown = false
        }

    }
    Box(modifier = modifier.fillMaxWidth()) {
        BoxWithConstraints(modifier = Modifier.wrapContentHeight()) {
            fieldWidth = maxWidth
            QuerySearch(
                query = state.query,
                label = state.queryLabel,
                error = state.errorText,
                onQueryChanged =
                {
                    onQueryChanged(it)
                },
                onDoneActionClick = {
                    shouldShowDropdown = false
                    onDismissRequest()
                },
                onFocusChanged = { focused ->
                    onFocusChanged(focused)
                    shouldShowDropdown = focused && state.list.isNotEmpty()
                }

            )
        }

        AnimatedVisibility(visible = shouldShowDropdown) {
            Popup(
                popupPositionProvider = object : PopupPositionProvider {
                    override fun calculatePosition(
                        anchorBounds: IntRect,
                        windowSize: IntSize,
                        layoutDirection: LayoutDirection,
                        popupContentSize: IntSize
                    ): IntOffset {
                        val offset =
                            if (anchorBounds.bottom + popupContentSize.height > windowSize.height) {
                                IntOffset(
                                    anchorBounds.left,
                                    max(anchorBounds.top - popupContentSize.height, 0)
                                )
                            } else {
                                IntOffset(
                                    anchorBounds.left, anchorBounds.top + fieldHeight
                                )

                            }
                        return offset
                    }
                },
                properties = PopupProperties(
                    dismissOnClickOutside = true,
                ),

                onDismissRequest = {
                    onDismissRequest()
                }
            ) {
                LazyColumn(
                    modifier = modifier
                        .heightIn(max = TextFieldDefaults.MinHeight * state.textLines)
                        .widthIn(fieldWidth)
                        .border(BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface))
                        .background(MaterialTheme.colorScheme.inverseOnSurface),
                    state = lazyListState,
                ) {
                    items(state.list) {
                        Box(
                            Modifier
                                .padding(8.dp)
                                .clickable {
                                    shouldShowDropdown = false
                                    onItemClick(it)
                                }) {
                            itemContent(it)
                        }
                    }

                }

            }
        }
    }
}


@Composable
fun QuerySearch(
    query: String,
    label: String,
    error: String? = null,
    onDoneActionClick: () -> Unit = {},
    onQueryChanged: (String) -> Unit,
    onFocusChanged: (isFocused: Boolean) -> Unit = {},
) {


    var showClearButton by remember { mutableStateOf(false) }
    var textFieldValueState by remember(query) {
        mutableStateOf(
            TextFieldValue(text = query, selection = TextRange(query.length))
        )
    }
    OutlinedTextField(
        modifier = Modifier
            .onFocusChanged { focusState ->
                showClearButton = focusState.isFocused
                if (focusState.isFocused) {
                    textFieldValueState = textFieldValueState.copy(
                        selection = TextRange(0, textFieldValueState.text.length)
                    )
                }
                onFocusChanged(focusState.isFocused)
            },

        value = textFieldValueState,
        onValueChange = {
            textFieldValueState = it
            onQueryChanged(it.text)
        },
        label = { Text(text = label) },
        isError = error == null,
        supportingText = {
            error?.let {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = error,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge,
        trailingIcon = {
            if (showClearButton) {
                IconButton(onClick = {
                    textFieldValueState = textFieldValueState.copy(text = "")
                    onQueryChanged("")
                }) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Clear")
                }
            }

        },
        keyboardActions = KeyboardActions(onDone = {
            onDoneActionClick()
        }),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Text
        )
    )
}


@Preview(name = "Light", uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun AutoCompleteTextViewPreview() {
    Surface {
//    AutoCompleteTextView(initialQuery = "QueryString", queryLabel = "Preview", list = listOf("1", "2", "3")) {

        //}
    }
}
