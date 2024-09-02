package com.gerwalex.library.composables.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class SwitchWithSliderState(
    value: Float,
    val min: Float,
    var max: Float,
) {
    init {
        require(value in min..max) { "value is $value). must be between $min and $max" }
    }

    var value by mutableStateOf(value)
    var isChecked by mutableStateOf(false)

}

@Composable
fun rememberSwitchWithSliderState(value: Float, min: Float, max: Float): SwitchWithSliderState {
    return remember { SwitchWithSliderState(value, min, max) }
}

@Composable
fun SwitchWithSlider(
    switchTitle: String,
    sliderTitle: String,
    state: SwitchWithSliderState,
    onCheckChanged: suspend (Boolean) -> Unit,
    onValueChanged: suspend (Float) -> Unit
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .defaultMinSize(minHeight = 48.dp)
            .fillMaxWidth()
    ) {
        SettingSwitchItem(title = switchTitle,
            checked = state.isChecked,
            onCheckedChange = { isChecked ->
                onCheckChanged(isChecked)
            })
        Text(
            text = sliderTitle
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = state.min.toInt().toString())
            Slider(
                modifier = Modifier.weight(1f),
                enabled = state.isChecked,
                value = state.value,
                onValueChange = { state.value = it },
                valueRange = state.min..state.max,
                steps = ((state.max - state.min) / 5 - 1).toInt(),
                onValueChangeFinished = {
                    scope.launch {
                        onValueChanged(state.value)
                    }
                },
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.secondary,
                    activeTrackColor = MaterialTheme.colorScheme.secondary
                )
            )
            Text(text = state.max.toInt().toString())

        }
    }
}


@Composable
fun SwitchWithSlider(
    switchTitle: String,
    sliderTitle: String,
    value: Float,
    min: Float,
    max: Float,
    isChecked: Boolean = false,
    onCheckChanged: suspend (Boolean) -> Unit,
    onValueChanged: suspend (Float) -> Unit
) {
    var myValue by remember {
        mutableStateOf(value)
    }
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .defaultMinSize(minHeight = 48.dp)
            .fillMaxWidth()
    ) {
        SettingSwitchItem(title = switchTitle,
            checked = isChecked,
            onCheckedChange = { isChecked ->
                onCheckChanged(isChecked)
            })
        Text(
            text = sliderTitle
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = min.toInt().toString())
            Slider(
                modifier = Modifier.weight(1f),
                enabled = isChecked,
                value = myValue,
                onValueChange = { myValue = it },
                valueRange = min..max,
                steps = ((max - min) / 5 - 1).toInt(),
                onValueChangeFinished = {
                    scope.launch {
                        onValueChanged(myValue)
                    }
                },
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.secondary,
                    activeTrackColor = MaterialTheme.colorScheme.secondary
                )
            )
            Text(text = max.toInt().toString())

        }
    }
}
