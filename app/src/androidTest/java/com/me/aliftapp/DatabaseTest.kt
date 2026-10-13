package com.me.aliftapp

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.me.aliftapp.data.local.AppDatabase
import com.me.aliftapp.data.local.Exercise
import com.me.aliftapp.data.local.Session
import com.me.aliftapp.data.local.SessionWithSets
import com.me.aliftapp.data.local.User
import com.me.aliftapp.data.local.WorkoutSet
import com.me.aliftapp.data.local.dao.ExerciseDao
import com.me.aliftapp.data.local.dao.SessionDao
import com.me.aliftapp.data.local.dao.UserDao
import com.me.aliftapp.data.local.dao.WorkoutSetDao
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var database: AppDatabase
    private lateinit var sessionDao: SessionDao
    private lateinit var userDao: UserDao
    private lateinit var workoutSetDao: WorkoutSetDao
    private lateinit var exerciseDao: ExerciseDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        sessionDao = database.sessionDao()
        userDao = database.userDao()
        workoutSetDao = database.workoutSetDao()
        exerciseDao = database.exerciseDao()
    }

    @After
    @Throws(IOException::class)
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndReadSession() = runTest {
        val dummyUser = User(uuid = "test-uuid-1", name = "Alex", currentStreak = 0)
        val dummySession = Session(
            startTimestamp = 1000L,
            name = "Leg Day",
            uuid = dummyUser.uuid
        )

        userDao.insertUser(dummyUser)
        val insertedSessionId = sessionDao.insertSession(dummySession)

        val result: SessionWithSets = sessionDao.getSessionWithSets(insertedSessionId).first()
        assertEquals("Leg Day", result.session.name)
        assertEquals("test-uuid-1", result.session.uuid)
        assert(result.workoutSets.isEmpty())
    }

    @Test
    fun sessionAndSetsConnection() = runTest {
        val dummyExercise = Exercise("Bench Press", 2.5)
        exerciseDao.insertExercise(dummyExercise)

        val dummyUser = User(uuid = "test-uuid-2", name = "Sam", currentStreak = 5)
        userDao.insertUser(dummyUser)

        val dummySession = Session(
            startTimestamp = 1630000000L,
            name = "Push Day",
            uuid = dummyUser.uuid
        )

        val insertedSessionId = sessionDao.insertSession(dummySession)

        val dummySet1 = WorkoutSet(
            sessionId = insertedSessionId,
            weight = 60.0,
            reps = 10,
            exerciseName = "Bench Press",
            setOrder = 1,
            exerciseOrder = 1
        )
        workoutSetDao.insertWorkoutSet(dummySet1)

        val dummySet2 = WorkoutSet(
            sessionId = insertedSessionId,
            weight = 65.0,
            reps = 8,
            exerciseName = "Bench Press",
            setOrder = 2,
            exerciseOrder = 1
        )
        workoutSetDao.insertWorkoutSet(dummySet2)

        val result = sessionDao.getSessionWithSets(insertedSessionId).first()
        assertEquals(result.session.name, "Push Day")
        assert(result.workoutSets.size == 2)
        assert(result.workoutSets.all {
            it.sessionId == result.session.sessionId
        })
    }

    @Test
    fun deleteSetInSession() = runTest {
        val dummyExercise = Exercise("Bench Press", 2.5)
        exerciseDao.insertExercise(dummyExercise)

        val dummyUser = User(uuid = "test-uuid3", name = "Francisco", currentStreak = 14)
        userDao.insertUser(dummyUser)

        val dummySession = Session(
            startTimestamp = 1630000000L,
            name = "Push Day",
            uuid = dummyUser.uuid
        )
        val insertedSessionId = sessionDao.insertSession(dummySession)

        val dummySet1 = WorkoutSet(
            sessionId = insertedSessionId,
            weight = 60.0,
            reps = 10,
            exerciseName = "Bench Press",
            setOrder = 1,
            exerciseOrder = 1
        )
        val workoutSetId1 = workoutSetDao.insertWorkoutSet(dummySet1)

        val dummySet2 = WorkoutSet(
            sessionId = insertedSessionId,
            weight = 65.0,
            reps = 8,
            exerciseName = "Bench Press",
            setOrder = 2,
            exerciseOrder = 1
        )
        val workoutSetId2 = workoutSetDao.insertWorkoutSet(dummySet2)

        workoutSetDao.deleteWorkoutSetById(workoutSetId1)
        workoutSetDao.deleteWorkoutSetById(workoutSetId2)

        val result = sessionDao.getSessionWithSets(insertedSessionId).first()
        assert(result.workoutSets.isEmpty())
    }

    @Test
    fun deleteUserCascadeSessions() = runTest {
        val dummyUser = User(uuid = "test-uuid4", name = "António", currentStreak = 14)
        userDao.insertUser(dummyUser)

        val dummySession = Session(
            startTimestamp = 1630000000L,
            name = "Push Day",
            uuid = dummyUser.uuid
        )
        val insertedSessionId = sessionDao.insertSession(dummySession)

        userDao.deleteUserByUuid(dummyUser.uuid)
        val deletedSession = sessionDao.getSessionById(insertedSessionId)

        assertEquals(null, deletedSession)
    }
}