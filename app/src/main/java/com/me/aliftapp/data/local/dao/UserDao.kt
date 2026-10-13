package com.me.aliftapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.me.aliftapp.data.local.User

@Dao
interface UserDao {

    @Insert
    suspend fun insertUser(user: User)

    @Query("DELETE FROM user WHERE uuid = :userUuid")
    suspend fun deleteUserByUuid(userUuid: String)
}