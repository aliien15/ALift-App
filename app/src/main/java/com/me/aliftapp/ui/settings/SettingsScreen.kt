package com.me.aliftapp.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val settings by viewModel.settingsState.collectAsStateWithLifecycle()

    Column {
        Text(text = "Settings")

        Button(onClick = { viewModel.setDarkMode(!settings.isDarkMode) }) { Text("Dark Mode") }

        // TODO: weight unit button and rest timer

        Button(onClick = { viewModel.setKeepScreenAwake(!settings.keepScreenAwake) }) {
            Text("Keep screen awake")
        }

        Button(onClick = { viewModel.setAutoStartRestTimer(!settings.autoStartRestTimer) }) {
            Text("Auto start rest timer")
        }

        Button(onClick = { viewModel.setVibrateOnceRestIsOver(!settings.vibrateOnceRestIsOver) }) {
            Text("Vibrate once rest time is over")
        }

        Button(onClick = { viewModel.resetToDefaults() }) { Text("Reset all settings to default") }
    }
}
