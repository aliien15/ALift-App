package com.me.aliftapp.data.local.preferences

enum class WeightUnit {
    KG,
    LBS;

    fun next(): WeightUnit {
        return entries[(this.ordinal + 1) % entries.size]
    }
}
