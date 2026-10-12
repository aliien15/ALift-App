package com.me.aliftapp.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithSessions(
    @Embedded val user: User,

    @Relation(
        parentColumn = "uuid",
        entityColumn = "uuid"
    )
    val sessions: List<Session>
)