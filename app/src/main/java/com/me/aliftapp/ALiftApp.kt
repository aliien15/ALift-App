package com.me.aliftapp

import android.app.Application
import androidx.room.Room
import com.me.aliftapp.data.local.AppDatabase

class ALiftApp : Application() {
    val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "alift_database"
        ).build()
    }

    override fun onCreate() {
        super.onCreate()
    }
}