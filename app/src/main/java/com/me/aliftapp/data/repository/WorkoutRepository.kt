package com.me.aliftapp.data.repository

import com.me.aliftapp.data.local.Session
import com.me.aliftapp.data.local.SessionWithSets
import com.me.aliftapp.data.local.dao.SessionDao
import com.me.aliftapp.data.local.dao.UserDao
import com.me.aliftapp.data.local.dao.WorkoutSetDao
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(
    private val sessionDao: SessionDao,
    private val workoutSetDao: WorkoutSetDao,
    private val userDao: UserDao,
) {
    suspend fun getActiveSessionStream(): Flow<SessionWithSets?> {
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
}
