package com.me.aliftapp.tests

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
import java.io.IOException
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private val setTestUser = User(uuid = "set-test-user", name = "Sam", currentStreak = 0)
    private lateinit var database: AppDatabase
    private lateinit var sessionDao: SessionDao
    private lateinit var userDao: UserDao
    private lateinit var workoutSetDao: WorkoutSetDao
    private lateinit var exerciseDao: ExerciseDao

    @Before
    fun setup() {
        database =
            Room.inMemoryDatabaseBuilder(
                    ApplicationProvider.getApplicationContext(),
                    AppDatabase::class.java,
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
        val dummySession = Session(startTimestamp = 1000L, name = "Leg Day", uuid = dummyUser.uuid)

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

        val dummySession =
            Session(startTimestamp = 1630000000L, name = "Push Day", uuid = dummyUser.uuid)

        val insertedSessionId = sessionDao.insertSession(dummySession)

        val dummySet1 =
            WorkoutSet(
                sessionId = insertedSessionId,
                weight = 60.0,
                reps = 10,
                exerciseName = "Bench Press",
                setOrder = 1,
                exerciseOrder = 1,
            )
        workoutSetDao.insertWorkoutSet(dummySet1)

        val dummySet2 =
            WorkoutSet(
                sessionId = insertedSessionId,
                weight = 65.0,
                reps = 8,
                exerciseName = "Bench Press",
                setOrder = 2,
                exerciseOrder = 1,
            )
        workoutSetDao.insertWorkoutSet(dummySet2)

        val result = sessionDao.getSessionWithSets(insertedSessionId).first()
        assertEquals("Push Day", result.session.name)
        assert(result.workoutSets.size == 2)
        assert(result.workoutSets.all { it.sessionId == result.session.sessionId })
    }

    @Test
    fun deleteSetInSession() = runTest {
        val dummyExercise = Exercise("Bench Press", 2.5)
        exerciseDao.insertExercise(dummyExercise)

        val dummyUser = User(uuid = "test-uuid3", name = "Francisco", currentStreak = 14)
        userDao.insertUser(dummyUser)

        val dummySession =
            Session(startTimestamp = 1630000000L, name = "Push Day", uuid = dummyUser.uuid)
        val insertedSessionId = sessionDao.insertSession(dummySession)

        val dummySet1 =
            WorkoutSet(
                sessionId = insertedSessionId,
                weight = 60.0,
                reps = 10,
                exerciseName = "Bench Press",
                setOrder = 1,
                exerciseOrder = 1,
            )
        val workoutSetId1 = workoutSetDao.insertWorkoutSet(dummySet1)

        val dummySet2 =
            WorkoutSet(
                sessionId = insertedSessionId,
                weight = 65.0,
                reps = 8,
                exerciseName = "Bench Press",
                setOrder = 2,
                exerciseOrder = 1,
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

        val dummySession =
            Session(startTimestamp = 1630000000L, name = "Push Day", uuid = dummyUser.uuid)
        val insertedSessionId = sessionDao.insertSession(dummySession)

        userDao.deleteUserByUuid(dummyUser.uuid)
        val deletedSession = sessionDao.getSessionById(insertedSessionId)

        assertEquals(null, deletedSession)
    }

    @Test
    fun currentUserReflectsInsertAndDelete() = runTest {
        val user = User(uuid = "current-user", name = "Alex", currentStreak = 3)

        assertNull(userDao.getCurrentUser())
        userDao.insertUser(user)
        assertEquals(user, userDao.getCurrentUser())
        userDao.deleteUserByUuid(user.uuid)
        assertNull(userDao.getCurrentUser())
    }

    @Test
    fun insertSessionWithExistingIdReplacesSession() = runTest {
        val sessionId = insertSessionWithUser()
        val replacement =
            Session(
                sessionId = sessionId,
                startTimestamp = 2000L,
                name = "Leg Day",
                uuid = setTestUser.uuid,
            )

        assertEquals(sessionId, sessionDao.insertSession(replacement))
        assertEquals(replacement, sessionDao.getSessionById(sessionId))
    }

    @Test
    fun discardSessionCascadesToItsSetsOnly() = runTest {
        val (sessionId, otherSessionId) = insertSessionsWithSets()

        sessionDao.discardSession(sessionId)

        assertNull(sessionDao.getSessionById(sessionId))
        assertEquals(0, sessionDao.getLastSetNumberForExercise(sessionId, "Bench Press"))
        assertEquals(0, sessionDao.getLastSetNumberForExercise(sessionId, "Squat"))
        assertEquals(1, sessionDao.getSessionWithSets(otherSessionId).first().workoutSets.size)
    }

    @Test
    fun deleteAllSetsForSessionLeavesSessionAndOtherSets() = runTest {
        val (sessionId, otherSessionId) = insertSessionsWithSets()
        val before = sessionDao.getSessionWithSets(sessionId).first()
        val otherBefore = sessionDao.getSessionWithSets(otherSessionId).first()

        workoutSetDao.deleteAllSetsForSession(sessionId)

        assertEquals(
            before.copy(workoutSets = emptyList()),
            sessionDao.getSessionWithSets(sessionId).first(),
        )
        assertEquals(otherBefore, sessionDao.getSessionWithSets(otherSessionId).first())
    }

    @Test
    fun deleteExerciseForSessionLeavesOtherExercisesAndSessions() = runTest {
        val (sessionId, otherSessionId) = insertSessionsWithSets()
        val squat =
            sessionDao.getSessionWithSets(sessionId).first().workoutSets.single {
                it.exerciseName == "Squat"
            }
        val otherBefore = sessionDao.getSessionWithSets(otherSessionId).first()

        workoutSetDao.deleteExerciseForSession(sessionId, "Bench Press")

        assertEquals(listOf(squat), sessionDao.getSessionWithSets(sessionId).first().workoutSets)
        assertEquals(otherBefore, sessionDao.getSessionWithSets(otherSessionId).first())
    }

    @Test
    fun setCountsAndExerciseOrdersAreScopedToSessionAndExercise() = runTest {
        val (sessionId, otherSessionId) = insertSessionsWithSets()

        assertEquals(2, sessionDao.getLastSetNumberForExercise(sessionId, "Bench Press"))
        assertEquals(1, sessionDao.getLastSetNumberForExercise(sessionId, "Squat"))
        assertEquals(1, sessionDao.getLastSetNumberForExercise(otherSessionId, "Bench Press"))
        assertEquals(0, sessionDao.getLastSetNumberForExercise(otherSessionId, "Squat"))
        assertEquals(7, sessionDao.getLastExerciseNumber(sessionId))
        assertEquals(2, sessionDao.getLastExerciseNumber(otherSessionId))
        assertEquals(4, workoutSetDao.getExistingExerciseOrder(sessionId, "Bench Press"))
        assertEquals(7, workoutSetDao.getExistingExerciseOrder(sessionId, "Squat"))
        assertEquals(2, workoutSetDao.getExistingExerciseOrder(otherSessionId, "Bench Press"))
        assertNull(workoutSetDao.getExistingExerciseOrder(otherSessionId, "Squat"))

        workoutSetDao.deleteAllSetsForSession(sessionId)
        assertEquals(0, sessionDao.getLastExerciseNumber(sessionId))
    }

    private suspend fun insertSessionsWithSets(): Pair<Long, Long> {
        val sessionId = insertSessionWithUser()
        val otherSessionId =
            sessionDao.insertSession(
                Session(startTimestamp = 2000L, name = "Full Body", uuid = setTestUser.uuid)
            )
        exerciseDao.insertAllExercises(listOf(Exercise("Bench Press", 2.5), Exercise("Squat", 5.0)))
        val bench =
            WorkoutSet(
                sessionId = sessionId,
                exerciseName = "Bench Press",
                setOrder = 1,
                exerciseOrder = 4,
                weight = 60.0,
                reps = 10,
            )
        workoutSetDao.insertWorkoutSet(bench.copy(exerciseName = "Squat", exerciseOrder = 7))
        workoutSetDao.insertWorkoutSet(bench)
        workoutSetDao.insertWorkoutSet(bench.copy(setOrder = 3))
        workoutSetDao.insertWorkoutSet(bench.copy(sessionId = otherSessionId, exerciseOrder = 2))
        return sessionId to otherSessionId
    }

    private suspend fun insertSessionWithUser(): Long {
        userDao.insertUser(setTestUser)
        return sessionDao.insertSession(
            Session(startTimestamp = 1000L, name = "Push Day", uuid = setTestUser.uuid)
        )
    }
}
