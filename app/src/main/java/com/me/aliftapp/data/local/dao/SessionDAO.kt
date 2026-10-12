package com.me.aliftapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.me.aliftapp.data.local.Session
import com.me.aliftapp.data.local.SessionWithSets
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: Session)

    @Query("SELECT * FROM workout_session WHERE sessionId = :id")
    fun getSessionWithSets(id: Long): Flow<SessionWithSets>
}