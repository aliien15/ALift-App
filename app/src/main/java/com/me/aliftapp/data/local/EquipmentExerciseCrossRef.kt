package com.me.aliftapp.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "equipment_exercise_cross_ref",
    primaryKeys = ["equipmentName", "exerciseName"]
)
data class EquipmentExerciseCrossRef(
    val equipmentName: String,
    @ColumnInfo(index = true)
    val exerciseName: String
)
