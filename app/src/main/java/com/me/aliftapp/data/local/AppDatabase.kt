package com.me.aliftapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.me.aliftapp.data.local.dao.ExerciseDao
import com.me.aliftapp.data.local.dao.SessionDao
import com.me.aliftapp.data.local.dao.UserDao
import com.me.aliftapp.data.local.dao.WorkoutSetDao

@Database(
    entities = [
        Equipment::class,
        EquipmentExerciseCrossRef::class,
        Exercise::class,
        Session::class,
        User::class,
        WorkoutSet::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDao
    abstract fun sessionDao(): SessionDao
    abstract fun userDao(): UserDao
    abstract fun workoutSetDao() : WorkoutSetDao
}