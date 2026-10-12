package com.me.aliftapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import com.me.aliftapp.data.local.User

@Dao
interface UserDao {

    @Insert
    suspend fun insertUser(user: User)
}