package com.me.aliftapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.me.aliftapp.data.local.Exercise
import com.me.aliftapp.data.local.ExerciseWithEquipment
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {

    @Insert
    suspend fun insertExercise(exercise: Exercise)

    @Insert
    suspend fun insertAllExercises(exercises: List<Exercise>)

    @Transaction
    @Query("SELECT * FROM exercise")
    fun getAllExercisesWithEquipment(): Flow<List<ExerciseWithEquipment>>
}