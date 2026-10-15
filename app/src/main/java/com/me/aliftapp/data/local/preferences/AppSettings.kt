package com.me.aliftapp.data.local.preferences

data class AppSettings(
    val isDarkMode: Boolean = DEFAULT_DARK_MODE,
    val weightUnit: WeightUnit = DEFAULT_WEIGHT_UNIT,
    val restTimerSeconds: Int = DEFAULT_REST_TIMER_SECONDS,
    val keepScreenAwake: Boolean = DEFAULT_KEEP_SCREEN_AWAKE,
    val autoStartRestTimer: Boolean = DEFAULT_AUTO_START_REST_TIMER,
    val vibrateOnceRestIsOver: Boolean = DEFAULT_VIBRATE_ONCE_REST_IS_OVER
) {
    companion object {
        const val DEFAULT_DARK_MODE = false
        val DEFAULT_WEIGHT_UNIT = WeightUnit.KG // basically a const
        const val DEFAULT_REST_TIMER_SECONDS = 90
        const val DEFAULT_KEEP_SCREEN_AWAKE = false
        const val DEFAULT_AUTO_START_REST_TIMER = true
        const val DEFAULT_VIBRATE_ONCE_REST_IS_OVER = true
    }
}
