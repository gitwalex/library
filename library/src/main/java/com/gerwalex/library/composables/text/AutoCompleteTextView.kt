package com.gerwalex.library.composables.text

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
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

class AutoCompleteTextViewState<T>(
    val initialQuery: String,
    val textLines: Int = 5,
    list: List<T>,
) {
    var query by mutableStateOf(initialQuery)
    var list: List<T> by mutableStateOf(list)
    var errorText: String? by mutableStateOf(null)
    var shouldShowDropdown by mutableStateOf(false)
    var showClearButton by mutableStateOf(false)
    internal var isFocused by mutableStateOf(false)

}

@Composable
fun <T> rememberAutoCompleteTextViewState(
    initialQuery: String = "",
    list: List<T> = emptyList(),
    textLines: Int = 5,
): AutoCompleteTextViewState<T> {
    return remember {
        AutoCompleteTextViewState(
            initialQuery = initialQuery,
            list = list,
            textLines = textLines
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun <T> AutoCompleteTextView(
    state: AutoCompleteTextViewState<T>,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    onQueryChanged: (query: String) -> Unit = {},
    onDismissRequest: () -> Unit = {},
    onItemClick: (T) -> Unit = {},
    onFocusChanged: (isFocused: Boolean) -> Unit = {},
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Done
    ),
    keyboardActions: KeyboardActions = KeyboardActions(
        onDone = {
            onDismissRequest()
        }),

    itemContent: @Composable (T) -> Unit,
) {

    val focusManager = LocalFocusManager.current
    val lazyListState = rememberLazyListState()
    val fieldHeight = LocalDensity.current.dpToIntPx(OutlinedTextFieldDefaults.MinHeight)
    var textFieldWidth by remember { mutableIntStateOf(0) }

    var textFieldValueState by remember { mutableStateOf(TextFieldValue("")) }
    LaunchedEffect(key1 = state.query) {
        textFieldValueState =
            TextFieldValue(text = state.query, selection = TextRange(state.query.length))
    }

    DisposableEffect(key1 = Unit) {
        onDispose {
            state.shouldShowDropdown = false
        }

    }
    Box(modifier = modifier) {
        OutlinedTextField(
            placeholder = placeholder,
            modifier = Modifier
                .onFocusChanged { focusState ->
                    state.isFocused = focusState.isFocused
                    state.shouldShowDropdown = focusState.isFocused
                    state.showClearButton = focusState.isFocused
                    if (focusState.isFocused) {
                        textFieldValueState = textFieldValueState.copy(
                            selection = TextRange(0, textFieldValueState.text.length)
                        )
                    }
                    onFocusChanged(focusState.isFocused)
                },

            value = textFieldValueState,
            onValueChange = { value ->
                textFieldValueState = value
                onQueryChanged(value.text)
            },
            label = label,
            isError = state.errorText == null,
            supportingText = {
                state.errorText?.let { errorText ->
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = errorText,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            singleLine = singleLine,
            textStyle = textStyle,
            trailingIcon = {
                if (state.showClearButton) {
                    IconButton(onClick = {
                        textFieldValueState = textFieldValueState.copy(text = "")
                        onQueryChanged("")
                    }) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Clear")
                    }
                }

            },
            keyboardActions = keyboardActions,
            keyboardOptions = keyboardOptions
        )
        SideEffect {
            Log.d("AutoCompleteTextView", "textFieldWidth: $textFieldWidth ")
        }
        AnimatedVisibility(visible = state.shouldShowDropdown) {
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

                        textFieldWidth = (anchorBounds.right - anchorBounds.left)
                        Log.d("AutoCompleteTextView", "windowSize: $windowSize")
                        Log.d("AutoCompleteTextView", "anchorBounds: $anchorBounds")
                        Log.d("AutoCompleteTextView", "popupContentSize: $popupContentSize")
                        Log.d("AutoCompleteTextView", "textFieldWidth: $textFieldWidth")
                        Log.d("AutoCompleteTextView", "fieldHeight: $fieldHeight")
                        return offset
                    }
                },
                properties = PopupProperties(
                    dismissOnClickOutside = false,
                    usePlatformDefaultWidth = false,
                ),
                onDismissRequest = {
                    onDismissRequest()
                    focusManager.clearFocus(true)
                }
            ) {
                LazyColumn(
                    modifier = modifier
                        .heightIn(max = TextFieldDefaults.MinHeight * state.textLines)
                        .width(200.dp)
                        .border(BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface))
                        .background(MaterialTheme.colorScheme.inverseOnSurface),
                    state = lazyListState,
                ) {
                    items(state.list) {
                        Box(
                            Modifier
                                .padding(8.dp)
                                .clickable {
                                    onItemClick(it)
                                    state.shouldShowDropdown = false
                                }) {
                            itemContent(it)
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Light", uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun AutoCompleteTextViewPreview() {
    Surface {
        val autoCompleteTextViewState = rememberAutoCompleteTextViewState(
            initialQuery = "QueryString",
            list = listOf("QueryString1", "QueryString2", "QueryString3")
        )
        Column(modifier = Modifier.fillMaxSize()) {
            Text("Text")

            AutoCompleteTextView(state = autoCompleteTextViewState) {
            }
        }
    }
}
