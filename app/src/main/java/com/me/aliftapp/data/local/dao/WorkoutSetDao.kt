package com.me.aliftapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import com.me.aliftapp.data.local.WorkoutSet

@Dao
interface WorkoutSetDao {

    @Insert
    suspend fun insertWorkoutSet(workoutSet: WorkoutSet): Long
}