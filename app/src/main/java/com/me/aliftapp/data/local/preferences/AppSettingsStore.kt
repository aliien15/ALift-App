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
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { preferences ->
        val isDarkMode = preferences[PreferencesKeys.IS_DARK_MODE] ?: false
        val weightUnitString = preferences[PreferencesKeys.WEIGHT_UNIT] ?: WeightUnit.KG.name
        val weightUnit = try {
            WeightUnit.valueOf(weightUnitString)
        } catch (_: IllegalArgumentException) {
            WeightUnit.KG
        }
        val restTimer = preferences[PreferencesKeys.REST_TIMER] ?: 90

        AppSettings(isDarkMode, weightUnit, restTimer)
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
}