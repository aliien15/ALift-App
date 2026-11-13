package com.me.aliftapp.data.repository

import com.me.aliftapp.data.local.Session
import com.me.aliftapp.data.local.SessionWithSets
import com.me.aliftapp.data.local.WorkoutSet
import com.me.aliftapp.data.local.dao.SessionDao
import com.me.aliftapp.data.local.dao.UserDao
import com.me.aliftapp.data.local.dao.WorkoutSetDao
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(
    private val sessionDao: SessionDao,
    private val workoutSetDao: WorkoutSetDao,
    private val userDao: UserDao,
) {
    fun getActiveSessionStream(): Flow<SessionWithSets?> {
        return sessionDao.getActiveSessionWithSets()
    }

    suspend fun startNewSession(name: String): Long {
        val newSession =
            Session(
                name = name,
                startTimestamp = System.currentTimeMillis(),
                uuid =
                    userDao.getCurrentUser()?.uuid
                        ?: throw IllegalStateException(
                            "Cannot start workout: user not found in the database."
                        ),
            )

        return sessionDao.insertSession(newSession)
    }

    suspend fun addSetToSession(sessionId: Long, exerciseName: String) {
        val nextSetOrder = sessionDao.getLastSetNumberForExercise(sessionId, exerciseName) + 1
        val existingOrder = workoutSetDao.getExistingExerciseOrder(sessionId, exerciseName)
        val finalExerciseOrder = existingOrder ?: (sessionDao.getLastExerciseNumber(sessionId) + 1)

        val newSet =
            WorkoutSet(
                sessionId = sessionId,
                exerciseName = exerciseName,
                setOrder = nextSetOrder,
                exerciseOrder = finalExerciseOrder,
                weight = 0.0,
                reps = 0,
            )
        workoutSetDao.insertWorkoutSet(newSet)
    }

    suspend fun updateSet(set: WorkoutSet) {
        workoutSetDao.updateSet(set)
    }

    suspend fun finishSession(session: Session) {
        val completeSession = session.copy(endTimestamp = System.currentTimeMillis())
        sessionDao.updateSession(completeSession)
    }
}
