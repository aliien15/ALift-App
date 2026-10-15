package com.me.aliftapp.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private val Context.dataStore by preferencesDataStore(name = "settings")

class AppSettingsStore(private val context: Context) {

    private object PreferencesKeys {
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val WEIGHT_UNIT = stringPreferencesKey("weight_unit")
        val REST_TIMER = intPreferencesKey("rest_timer")
        val KEEP_SCREEN_AWAKE = booleanPreferencesKey("keep_screen_awake")
        val AUTO_START_REST_TIMER = booleanPreferencesKey("auto_start_rest_timer")
        val VIBRATE_ONCE_REST_TIME_IS_OVER = booleanPreferencesKey("vibrate_once_rest_time_is_over")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { preferences ->
        val isDarkMode = preferences[PreferencesKeys.IS_DARK_MODE] ?: AppSettings.DEFAULT_DARK_MODE
        val weightUnitString = preferences[PreferencesKeys.WEIGHT_UNIT] ?: AppSettings.DEFAULT_WEIGHT_UNIT.name
        val weightUnit = try {
            WeightUnit.valueOf(weightUnitString)
        } catch (_: IllegalArgumentException) {
            AppSettings.DEFAULT_WEIGHT_UNIT
        }
        val restTimer = preferences[PreferencesKeys.REST_TIMER] ?: AppSettings.DEFAULT_REST_TIMER_SECONDS
        val keepScreenAwake = preferences[PreferencesKeys.KEEP_SCREEN_AWAKE] ?: AppSettings.DEFAULT_KEEP_SCREEN_AWAKE
        val autoStartRestTimer = preferences[PreferencesKeys.AUTO_START_REST_TIMER] ?: AppSettings.DEFAULT_AUTO_START_REST_TIMER
        val vibrateOnceRestTimeIsOver = preferences[PreferencesKeys.VIBRATE_ONCE_REST_TIME_IS_OVER] ?: AppSettings.DEFAULT_VIBRATE_ONCE_REST_IS_OVER

        AppSettings(isDarkMode, weightUnit, restTimer, keepScreenAwake, autoStartRestTimer, vibrateOnceRestTimeIsOver)
    }

    suspend fun setAllSettingsToDefault() {
        context.dataStore.edit { preferences -> preferences.clear() }
    }

    suspend fun setDarkMode(darkMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_DARK_MODE] = darkMode
        }
    }

    suspend fun setWeightUnit(weightUnit: WeightUnit) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.WEIGHT_UNIT] = weightUnit.name
        }
    }

    suspend fun setRestTimer(restTimer: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.REST_TIMER] = restTimer
        }
    }

    suspend fun setKeepScreenAwake(keepAwake: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEEP_SCREEN_AWAKE] = keepAwake
        }
    }

    suspend fun setAutoStartRestTimer(autoStart: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_START_REST_TIMER] = autoStart
        }
    }

    suspend fun setVibrateOnceRestIsOver(vibrate: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.VIBRATE_ONCE_REST_TIME_IS_OVER] = vibrate
        }
    }
}