package com.me.aliftapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "equipment")
data class Equipment(
    @PrimaryKey val equipmentName: String
)
