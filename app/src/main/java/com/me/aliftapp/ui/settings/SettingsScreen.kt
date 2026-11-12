package com.me.aliftapp.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val settings by viewModel.settingsState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            Modifier.fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .statusBarsPadding()
                .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.outlineVariant)

        Column(
            modifier =
                Modifier.fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SwitchSettingRow(
                label = "Dark Mode",
                isChecked = settings.isDarkMode,
                onCheckedChange = viewModel::setDarkMode,
            )

            SettingBox(label = "Weight Unit: ${settings.weightUnit.name}") {
                Button(onClick = { viewModel.setWeightUnit(settings.weightUnit.next()) }) {
                    Text(text = "Switch Unit")
                }
            }

            SettingBox(label = "Rest Timer: ${settings.restTimerSeconds}s") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            viewModel.setRestTimer(
                                (settings.restTimerSeconds - 15).coerceAtLeast(0)
                            )
                        }
                    ) {
                        Text(text = "-15s")
                    }

                    Button(
                        onClick = {
                            viewModel.setRestTimer(
                                (settings.restTimerSeconds + 15).coerceAtMost(600)
                            )
                        }
                    ) {
                        Text(text = "+15s")
                    }
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

            Button(
                onClick = { viewModel.resetToDefaults() },
                modifier = Modifier.fillMaxWidth(),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            MaterialTheme.colorScheme
                                .surface, // Beige (Light mode) / Lighter Gray (Dark mode)
                        contentColor =
                            MaterialTheme.colorScheme
                                .onSurface, // Black text (Light mode) / White text (Dark mode)
                    ),
            ) {
                Text("Reset all settings to default")
            }
        }
    }
}

@Composable
private fun SwitchSettingRow(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    SettingBox(label = label) {
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors =
                SwitchDefaults.colors(
                    // Colors when OFF
                    uncheckedTrackColor =
                        MaterialTheme.colorScheme.outlineVariant, // Light Gray track
                    uncheckedThumbColor = MaterialTheme.colorScheme.background, // White thumb
                    uncheckedBorderColor =
                        MaterialTheme.colorScheme.surfaceTint, // Removes the muddy border

                    // Colors when ON
                    checkedTrackColor = MaterialTheme.colorScheme.primary, // White track
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary, // Black thumb
                ),
        )
    }
}

@Composable
private fun SettingBox(label: String, content: @Composable () -> Unit) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = label)
            content()
        }
    }
}
