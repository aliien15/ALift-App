package com.me.aliftapp.data.local.preferences

data class AppSettings(
    val isDarkMode: Boolean = false,
    val weightUnit: WeightUnit = WeightUnit.KG,
    val restTimerSeconds: Int = 90
)
