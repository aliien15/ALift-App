package com.me.aliftapp.data.repository

import com.me.aliftapp.data.local.ExerciseWithEquipment
import com.me.aliftapp.data.local.dao.ExerciseDao
import kotlinx.coroutines.flow.Flow

class ExerciseRepository(private val exerciseDao: ExerciseDao) {
    fun getAllExercisesWithEquipment(): Flow<List<ExerciseWithEquipment>> {
        return exerciseDao.getAllExercisesWithEquipment()
    }

    fun getSearchedExercisesWithEquipment(query: String): Flow<List<ExerciseWithEquipment>> {
        return exerciseDao.getAllSearchedExercisesWithEquipment(query)
    }
}
