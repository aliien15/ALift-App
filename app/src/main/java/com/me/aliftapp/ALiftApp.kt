package com.me.aliftapp

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.db.SupportSQLiteDatabase
import com.me.aliftapp.data.local.AppDatabase
import com.me.aliftapp.data.local.Exercise
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.BufferedReader

class ALiftApp : Application() {
    val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "alift_database"
        ).addCallback(object: RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    populateInitialData()
                }
            }
        }).build()
    }

    private suspend fun populateInitialData() {
        try {
            val jsonString = applicationContext.assets.open("exercises.json")
                .bufferedReader()
                .use(BufferedReader::readText)

            val jsonArray = JSONArray(jsonString)
            val exercises: List<Exercise> = (0 until jsonArray.length()).map { index ->
                val exercise = jsonArray.getJSONObject(index)
                val exerciseName = exercise.getString("exerciseName")
                val progressionWeight = exercise.getDouble("progressionWeight")

                Exercise(exerciseName, progressionWeight)
            }

            database.exerciseDao().insertAllExercises(exercises)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreate() {
        super.onCreate()
    }
}