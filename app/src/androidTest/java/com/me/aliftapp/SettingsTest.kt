package com.me.aliftapp

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.me.aliftapp.data.local.preferences.AppSettings
import com.me.aliftapp.data.local.preferences.AppSettingsStore
import com.me.aliftapp.data.local.preferences.WeightUnit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@Suppress("KotlinConstantConditions")
@RunWith(AndroidJUnit4::class)
class SettingsTest {
    private lateinit var settings: AppSettingsStore
    private val invertedDarkMode = !AppSettings.DEFAULT_DARK_MODE
    private val invertedWeightUnit = if (AppSettings.DEFAULT_WEIGHT_UNIT == WeightUnit.KG) WeightUnit.LBS else WeightUnit.KG
    private val invertedRestTimer = AppSettings.DEFAULT_REST_TIMER_SECONDS + 90
    private val invertedKeepScreenAwake = !AppSettings.DEFAULT_KEEP_SCREEN_AWAKE
    private val invertedAutoStartRestTimer = !AppSettings.DEFAULT_AUTO_START_REST_TIMER
    private val invertedVibrateOnceRestIsOver = !AppSettings.DEFAULT_VIBRATE_ONCE_REST_IS_OVER
    private val invertedDefaultSettings = AppSettings(
        invertedDarkMode,
        invertedWeightUnit,
        invertedRestTimer,
        invertedKeepScreenAwake,
        invertedAutoStartRestTimer,
        invertedVibrateOnceRestIsOver
    )

    @Before
    fun setup() {
        settings = AppSettingsStore(ApplicationProvider.getApplicationContext())
        runBlocking { settings.setAllSettingsToDefault() }
    }

    @Test
    fun allDefaultValuesMatch() = runTest {
        val settingsFlow = settings.settingsFlow.first()

        assertEquals(AppSettings(), settingsFlow)
    }

    @Test
    fun modifySettingsStatesPersists() = runTest {
        settings.setDarkMode(invertedDarkMode)
        settings.setWeightUnit(invertedWeightUnit)
        settings.setRestTimer(invertedRestTimer)
        settings.setKeepScreenAwake(invertedKeepScreenAwake)
        settings.setAutoStartRestTimer(invertedAutoStartRestTimer)
        settings.setVibrateOnceRestIsOver(invertedVibrateOnceRestIsOver)
        val settingsFlow = settings.settingsFlow.first()

        assertEquals(invertedDefaultSettings, settingsFlow)
    }

    @Test
    fun resettingValuesActuallyWorks() = runTest {
        settings.setDarkMode(invertedDarkMode)
        settings.setWeightUnit(invertedWeightUnit)
        settings.setRestTimer(invertedRestTimer)
        settings.setKeepScreenAwake(invertedKeepScreenAwake)
        settings.setAutoStartRestTimer(invertedAutoStartRestTimer)
        settings.setVibrateOnceRestIsOver(invertedVibrateOnceRestIsOver)

        settings.setAllSettingsToDefault()
        val settingsFlow = settings.settingsFlow.first()

        assertEquals(AppSettings(), settingsFlow)
    }
}