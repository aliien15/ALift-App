package com.me.aliftapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.me.aliftapp.data.local.dao.ExerciseDAO
import com.me.aliftapp.data.local.dao.SessionDAO

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

    abstract fun exerciseDAO(): ExerciseDAO
    abstract fun SessionDAO(): SessionDAO
}