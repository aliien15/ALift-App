package com.me.aliftapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise")
data class Exercise(
    @PrimaryKey val exerciseName: String,
    val progressionWeight: Double
)
