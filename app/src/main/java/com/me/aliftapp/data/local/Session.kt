package com.me.aliftapp.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_session",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["uuid"],
            childColumns = ["uuid"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("uuid")]
)
data class Session(
    @PrimaryKey(autoGenerate = true) val sessionId: Long = 0,
    val startTimestamp: Long,
    val endTimestamp: Long? = null,
    val name: String,
    val uuid: String
)
