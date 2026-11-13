package com.me.aliftapp.tests

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.me.aliftapp.data.local.AppDatabase
import com.me.aliftapp.data.local.Equipment
import com.me.aliftapp.data.local.Exercise
import com.me.aliftapp.data.repository.ExerciseRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExerciseRepositoryTest {
    private lateinit var database: AppDatabase
    private lateinit var repository: ExerciseRepository

    @Before
    fun setup() {
        database =
            Room.inMemoryDatabaseBuilder(
                    ApplicationProvider.getApplicationContext(),
                    AppDatabase::class.java,
                )
                .allowMainThreadQueries()
                .build()
        repository = ExerciseRepository(database.exerciseDao())
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun emptyDatabaseReturnsNoExercises() = runTest {
        assertTrue(repository.getAllExercisesWithEquipment().first().isEmpty())
        assertTrue(repository.getSearchedExercisesWithEquipment("press").first().isEmpty())
    }

    @Test
    fun allExercisesStreamReflectsInsertedExercises() = runTest {
        val stream = repository.getAllExercisesWithEquipment()
        assertTrue(stream.first().isEmpty())
        val exercises = listOf(Exercise("Bench Press", 2.5), Exercise("Squat", 5.0))
        database.exerciseDao().insertAllExercises(exercises)

        val result = stream.first()
        assertEquals(exercises.toSet(), result.map { it.exercise }.toSet())
        assertTrue(result.all { it.equipments.isEmpty() })
    }

    @Test
    fun searchMatchesSubstringIgnoringCase() = runTest {
        database
            .exerciseDao()
            .insertAllExercises(
                listOf(
                    Exercise("Bench Press", 2.5),
                    Exercise("Overhead Press", 1.0),
                    Exercise("Squat", 5.0),
                )
            )

        val result = repository.getSearchedExercisesWithEquipment("pReSs").first()

        assertEquals(
            setOf("Bench Press", "Overhead Press"),
            result.map { it.exercise.exerciseName }.toSet(),
        )
    }

    @Test
    fun allAndSearchResultsIncludeOnlyTheExercisesRelatedEquipment() = runTest {
        database
            .exerciseDao()
            .insertAllExercises(listOf(Exercise("Bench Press", 2.5), Exercise("Squat", 5.0)))
        // There is no equipment DAO, so seed the relation tables directly.
        val sqlite = database.openHelper.writableDatabase
        listOf("Barbell", "Bench", "Rack").forEach { name ->
            sqlite.execSQL("INSERT INTO equipment (equipmentName) VALUES (?)", arrayOf(name))
        }
        listOf("Barbell" to "Bench Press", "Bench" to "Bench Press", "Rack" to "Squat").forEach {
            (equipment, exercise) ->
            sqlite.execSQL(
                "INSERT INTO equipment_exercise_cross_ref (equipmentName, exerciseName) VALUES (?, ?)",
                arrayOf(equipment, exercise),
            )
        }

        val all = repository.getAllExercisesWithEquipment().first()
        val bench = all.single { it.exercise.exerciseName == "Bench Press" }
        assertEquals(setOf(Equipment("Barbell"), Equipment("Bench")), bench.equipments.toSet())
        assertEquals(
            listOf(Equipment("Rack")),
            all.single { it.exercise.exerciseName == "Squat" }.equipments,
        )
        val searched = repository.getSearchedExercisesWithEquipment("Bench").first().single()
        assertEquals(bench.exercise, searched.exercise)
        assertEquals(bench.equipments.toSet(), searched.equipments.toSet())
    }

    @Test
    fun searchWithNoMatchReturnsEmptyList() = runTest {
        database.exerciseDao().insertExercise(Exercise("Squat", 5.0))

        assertTrue(repository.getSearchedExercisesWithEquipment("deadlift").first().isEmpty())
    }

    @Test
    fun emptySearchReturnsAllExercises() = runTest {
        val exercises = listOf(Exercise("Bench Press", 2.5), Exercise("Squat", 5.0))
        database.exerciseDao().insertAllExercises(exercises)

        assertEquals(
            exercises.toSet(),
            repository.getSearchedExercisesWithEquipment("").first().map { it.exercise }.toSet(),
        )
    }
}
