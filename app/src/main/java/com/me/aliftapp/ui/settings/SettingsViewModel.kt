package com.me.aliftapp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.me.aliftapp.data.local.preferences.AppSettings
import com.me.aliftapp.data.local.preferences.AppSettingsStore
import com.me.aliftapp.data.local.preferences.WeightUnit
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val settings: AppSettingsStore) : ViewModel() {

    val settingsState: StateFlow<AppSettings> =
        settings.settingsFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings(),
        )

    fun setDarkMode(isDark: Boolean) {
        viewModelScope.launch { settings.setDarkMode(isDark) }
    }

    fun setWeightUnit(weightUnit: WeightUnit) {
        viewModelScope.launch { settings.setWeightUnit(weightUnit) }
    }

    fun setRestTimer(restTimer: Int) {
        viewModelScope.launch { settings.setRestTimer(restTimer) }
    }

    fun setKeepScreenAwake(keepAwake: Boolean) {
        viewModelScope.launch { settings.setKeepScreenAwake(keepAwake) }
    }

    fun setAutoStartRestTimer(autoStart: Boolean) {
        viewModelScope.launch { settings.setAutoStartRestTimer(autoStart) }
    }

    fun setVibrateOnceRestIsOver(vibrate: Boolean) {
        viewModelScope.launch { settings.setVibrateOnceRestIsOver(vibrate) }
    }

    fun resetToDefaults() {
        viewModelScope.launch { settings.setAllSettingsToDefault() }
    }

    companion object {
        fun provideFactory(settingsStore: AppSettingsStore): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { SettingsViewModel(settingsStore) }
            }
    }
}
