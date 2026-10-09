package com.me.aliftapp.data.local

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ExerciseWithEquipment(
    @Embedded val exercise: Exercise,

    @Relation(
        parentColumn = "exerciseName",
        entityColumn = "equipmentName",
        associateBy = Junction(EquipmentExerciseCrossRef::class)
    )
    val equipments: List<Equipment>
)
