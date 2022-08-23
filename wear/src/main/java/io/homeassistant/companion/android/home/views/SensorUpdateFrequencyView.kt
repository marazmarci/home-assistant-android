package io.homeassistant.companion.android.home.views

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import com.mikepenz.iconics.compose.Image
import com.mikepenz.iconics.typeface.library.community.material.CommunityMaterial
import io.homeassistant.companion.android.common.R
import io.homeassistant.companion.android.database.settings.SensorUpdateFrequencySetting
import io.homeassistant.companion.android.theme.wearColorPalette
import io.homeassistant.companion.android.views.ListHeader
import kotlinx.coroutines.launch
import kotlin.math.sign

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SensorUpdateFrequencyView(
    currentSensorUpdateFrequency: SensorUpdateFrequencySetting,
    onSelectSensorUpdateFrequency: (SensorUpdateFrequencySetting) -> Unit
) {
    val options = listOf(
        SensorUpdateFrequencySetting.NORMAL,
        SensorUpdateFrequencySetting.FAST_WHILE_CHARGING,
        SensorUpdateFrequencySetting.FAST_ALWAYS,
    )
    val initialIndex = options.indexOf(currentSensorUpdateFrequency)
    val state = rememberPickerState(
        initialNumberOfOptions = options.size,
        initiallySelectedOption = if (initialIndex != -1) initialIndex else 0,
        repeatItems = true
    )
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.sensor_update_frequency_description),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        ListHeader(R.string.sensor_update_frequency)
        Picker(
            state = state,
            modifier = Modifier
                .weight(1f)
                .padding(all = 8.dp)
                .onRotaryScrollEvent {
                    coroutineScope.launch {
                        state.scrollToOption(
                            state.selectedOption + it.verticalScrollPixels.sign.toInt()
                        )
                    }
                    true
                }
                .focusRequester(focusRequester)
                .focusable()
        ) {
            Text(
                when (options[it]) {
                    SensorUpdateFrequencySetting.NORMAL -> R.string.sensor_update_frequency_normal
                    SensorUpdateFrequencySetting.FAST_WHILE_CHARGING -> R.string.sensor_update_frequency_fast_charging
                    SensorUpdateFrequencySetting.FAST_ALWAYS -> R.string.sensor_update_frequency_fast_always
                }.let { stringResId ->
                    stringResource(stringResId)
                }.substringBefore("\n"),
                fontSize = 24.sp,
                color = if (it != this.selectedOption) wearColorPalette.onBackground else wearColorPalette.primary
            )
        }
        Button(
            onClick = { onSelectSensorUpdateFrequency(options[state.selectedOption]) },
            colors = ButtonDefaults.primaryButtonColors(),
            modifier = Modifier
        ) {
            Image(
                CommunityMaterial.Icon.cmd_check
            )
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}
