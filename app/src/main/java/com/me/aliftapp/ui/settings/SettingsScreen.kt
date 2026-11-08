package com.me.aliftapp.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val settings by viewModel.settingsState.collectAsStateWithLifecycle()

    Column {
        Text(text = "Settings")

        SwitchSettingRow(
            label = "Dark Mode",
            isChecked = settings.isDarkMode,
            onCheckedChange = viewModel::setDarkMode,
        )

        Row {
            Text(text = "Weight Unit: ${settings.weightUnit.name}")

            Button(onClick = { viewModel.setWeightUnit(settings.weightUnit.next()) }) {
                Text(text = "Switch Unit")
            }
        }

        Row {
            Text(text = "Rest Timer: ${settings.restTimerSeconds}")

            Button(
                onClick = {
                    viewModel.setRestTimer((settings.restTimerSeconds - 15).coerceAtLeast(0))
                }
            ) {
                Text(text = "-15s")
            }

            Button(
                onClick = {
                    viewModel.setRestTimer((settings.restTimerSeconds + 15).coerceAtMost(600))
                }
            ) {
                Text(text = "+15s")
            }
        }

        SwitchSettingRow(
            label = "Keep Screen Awake",
            isChecked = settings.keepScreenAwake,
            onCheckedChange = viewModel::setKeepScreenAwake,
        )

        SwitchSettingRow(
            label = "Auto Start Rest Timer",
            isChecked = settings.autoStartRestTimer,
            onCheckedChange = viewModel::setAutoStartRestTimer,
        )

        SwitchSettingRow(
            label = "Vibrate Once Rest Time Is Over",
            isChecked = settings.vibrateOnceRestIsOver,
            onCheckedChange = viewModel::setVibrateOnceRestIsOver,
        )

        Button(onClick = { viewModel.resetToDefaults() }) { Text("Reset all settings to default") }
    }
}

@Composable
private fun SwitchSettingRow(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row {
        Text(text = label)
        Switch(checked = isChecked, onCheckedChange = onCheckedChange)
    }
}
