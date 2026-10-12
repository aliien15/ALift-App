package com.me.aliftapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "user")
data class User(
    @PrimaryKey val uuid: String = UUID.randomUUID().toString(),
    val name: String,
    val currentStreak: Int
)
