package com.me.aliftapp.tests

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.me.aliftapp.data.local.AppDatabase
import com.me.aliftapp.data.local.Exercise
import com.me.aliftapp.data.local.Session
import com.me.aliftapp.data.local.User
import com.me.aliftapp.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WorkoutRepositoryTest {
    private lateinit var database: AppDatabase
    private lateinit var repository: WorkoutRepository
    private val user = User(uuid = "repository-test-user", name = "Alex", currentStreak = 0)

    @Before
    fun setup() {
        database =
            Room.inMemoryDatabaseBuilder(
                    ApplicationProvider.getApplicationContext(),
                    AppDatabase::class.java,
                )
                .allowMainThreadQueries()
                .build()
        repository =
            WorkoutRepository(database.sessionDao(), database.workoutSetDao(), database.userDao())
    }

    @After
    fun teardown() {
        database.close()
    }

    private suspend fun startSession(name: String = "Push Day"): Long {
        database.userDao().insertUser(user)
        database
            .exerciseDao()
            .insertAllExercises(
                listOf(Exercise("Bench Press", 2.5), Exercise("Overhead Press", 1.0))
            )
        return repository.startNewSession(name)
    }

    @Test
    fun activeSessionIsNullWhenDatabaseIsEmpty() = runTest {
        assertNull(repository.getActiveSessionStream().first())
    }

    @Test
    fun startingSessionPersistsNameUserAndStartTime() = runTest {
        val before = System.currentTimeMillis()
        val sessionId = startSession()
        val after = System.currentTimeMillis()

        assertTrue(sessionId > 0)
        val session = database.sessionDao().getSessionById(sessionId)
        assertNotNull(session)
        assertEquals("Push Day", session!!.name)
        assertEquals(user.uuid, session.uuid)
        assertTrue(session.startTimestamp in before..after)
        assertNull(session.endTimestamp)
        val active = repository.getActiveSessionStream().first()
        assertEquals(session, active?.session)
        assertTrue(active!!.workoutSets.isEmpty())
    }

    @Test
    fun startingSessionWithoutUserThrowsAndDoesNotCreateSession() = runTest {
        try {
            repository.startNewSession("Push Day")
            fail("Expected a missing-user error")
        } catch (error: IllegalStateException) {
            assertEquals("Cannot start workout: user not found in the database.", error.message)
        }
        assertNull(repository.getActiveSessionStream().first())
    }

    @Test
    fun firstSetStartsAtOneWithZeroWeightAndReps() = runTest {
        val sessionId = startSession()

        repository.addSetToSession(sessionId, "Bench Press")

        val set = database.sessionDao().getSessionWithSets(sessionId).first().workoutSets.single()
        assertTrue(set.setId > 0)
        assertEquals(sessionId, set.sessionId)
        assertEquals("Bench Press", set.exerciseName)
        assertEquals(1, set.setOrder)
        assertEquals(1, set.exerciseOrder)
        assertEquals(0.0, set.weight, 0.0)
        assertEquals(0, set.reps)
    }

    @Test
    fun addingSetsKeepsExerciseOrderAndNumbersEachExerciseSeparately() = runTest {
        val sessionId = startSession()
        repository.addSetToSession(sessionId, "Bench Press")
        repository.addSetToSession(sessionId, "Overhead Press")
        repository.addSetToSession(sessionId, "Bench Press")

        val sets = database.sessionDao().getSessionWithSets(sessionId).first().workoutSets
        assertEquals(3, sets.size)
        val benchSets = sets.filter { it.exerciseName == "Bench Press" }
        assertEquals(listOf(1, 2), benchSets.map { it.setOrder }.sorted())
        assertTrue(benchSets.all { it.exerciseOrder == 1 })
        val overheadSet = sets.single { it.exerciseName == "Overhead Press" }
        assertEquals(1, overheadSet.setOrder)
        assertEquals(2, overheadSet.exerciseOrder)
    }

    @Test
    fun newSessionStartsItsOwnSetAndExerciseOrdering() = runTest {
        val firstId = startSession()
        repository.addSetToSession(firstId, "Bench Press")
        repository.addSetToSession(firstId, "Overhead Press")
        repository.finishSession(database.sessionDao().getSessionById(firstId)!!)
        val secondId = repository.startNewSession("Second workout")

        repository.addSetToSession(secondId, "Overhead Press")

        val set = database.sessionDao().getSessionWithSets(secondId).first().workoutSets.single()
        assertEquals(secondId, set.sessionId)
        assertEquals(1, set.setOrder)
        assertEquals(1, set.exerciseOrder)
        assertEquals(2, database.sessionDao().getSessionWithSets(firstId).first().workoutSets.size)
    }

    @Test
    fun updatingSetPersistsChangesWithoutInsertingAnotherSet() = runTest {
        val sessionId = startSession()
        repository.addSetToSession(sessionId, "Bench Press")
        val original =
            database.sessionDao().getSessionWithSets(sessionId).first().workoutSets.single()
        val updated = original.copy(weight = 62.5, reps = 8)

        repository.updateSet(updated)

        assertEquals(
            listOf(updated),
            database.sessionDao().getSessionWithSets(sessionId).first().workoutSets,
        )
        assertEquals(listOf(updated), repository.getActiveSessionStream().first()!!.workoutSets)
    }

    @Test
    fun finishingSessionSetsEndTimeAndClearsActiveSession() = runTest {
        val activeStream = repository.getActiveSessionStream()
        assertNull(activeStream.first())
        val sessionId = startSession()
        val original: Session = activeStream.first()!!.session
        repository.addSetToSession(sessionId, "Bench Press")
        val before = System.currentTimeMillis()

        repository.finishSession(original)

        val after = System.currentTimeMillis()
        val finished = database.sessionDao().getSessionById(sessionId)!!
        assertNotNull(finished.endTimestamp)
        assertTrue(finished.endTimestamp!! in before..after)
        assertEquals(original.copy(endTimestamp = finished.endTimestamp), finished)
        assertNull(activeStream.first())
        assertEquals(
            1,
            database.sessionDao().getSessionWithSets(sessionId).first().workoutSets.size,
        )
    }
}
