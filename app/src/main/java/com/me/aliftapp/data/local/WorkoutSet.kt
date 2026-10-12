package com.me.aliftapp.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_set",
    foreignKeys = [
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["exerciseName"],
            childColumns = ["exerciseName"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("exerciseName")]
)
data class WorkoutSet(
    @PrimaryKey(autoGenerate = true) val setId: Long,
    val exerciseName: String,
    val weight: Double,
    val reps: Int
)
