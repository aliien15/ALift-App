package com.me.aliftapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.me.aliftapp.data.local.WorkoutSet

@Dao
interface WorkoutSetDao {

    @Insert
    suspend fun insertWorkoutSet(workoutSet: WorkoutSet): Long

    @Query("DELETE FROM workout_set WHERE setId = :id")
    suspend fun deleteWorkoutSetById(id: Long)
}