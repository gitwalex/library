package com.gerwalex.example.ui.theme

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.gerwalex.library.composables.text.AutoCompleteTextView
import com.gerwalex.library.composables.text.rememberAutoCompleteTextViewState
import java.util.Locale

val languages: List<Locale> by lazy {
    Locale.getISOLanguages()
        .map {
            Locale(it)
        }.filter { loc ->
            //loc.language != Locale.getDefault().language &&
            loc.language.length == 2
        }.sortedBy {
            it.displayLanguage
        }
}
var language: String = Locale.getDefault().language


@Composable
fun AutoCompleteTextViewExample(
    modifier: Modifier = Modifier
) {
    var selectedLanguage = remember { language }
    val state = rememberAutoCompleteTextViewState(
        initialQuery = Locale(selectedLanguage).displayLanguage,
        list = languages
    )

    OutlinedCard(modifier = modifier.fillMaxWidth()) {
        Text(text = "AutoCompleteTextViewExample")
        AutoCompleteTextView(state = state,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.Start),
            placeholder = { Locale(selectedLanguage).displayLanguage },
            onQueryChanged = { value ->
                state.list = if (value.isEmpty()) {
                    languages.toList().also {
                        selectedLanguage = Locale.getDefault().language
                    }
                } else {
                    languages.toList().filter {
                        it.displayLanguage.lowercase().startsWith(value.lowercase())
                    }.also {
                        if (it.isNotEmpty()) {
                            selectedLanguage = it[0].language
                            state.errorText = null
                        } else {
                            state.errorText = "Keine passenden Sprachen gefunden"
                            selectedLanguage = Locale.getDefault().language
                        }
                    }
                }
                state.shouldShowDropdown = state.list.isNotEmpty()

                Log.d("InputCard", "value: $value, list: ${state.list.size}")
            },
            onFocusChanged = { isFocused ->
                if (!isFocused) {
                    state.query = Locale(selectedLanguage).displayLanguage
                }
            },
            onItemClick = { value ->
                state.query = value.displayLanguage
                selectedLanguage = value.language
            },
            label = { Text(text = "Sprache") }
        )
        {
            Text(text = it.displayLanguage)
        }
    }
}