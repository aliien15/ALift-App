package com.me.aliftapp.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class SessionWithSets(
    @Embedded val session: Session,

    @Relation(
        parentColumn = "sessionId",
        entityColumn = "sessionId"
    )
    val workoutSets: List<WorkoutSet>
)
