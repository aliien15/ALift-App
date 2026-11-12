package com.me.aliftapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.me.aliftapp.data.local.Session
import com.me.aliftapp.data.local.SessionWithSets
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: Session): Long

    @Transaction
    @Query("SELECT * FROM workout_session WHERE sessionId = :id")
    fun getSessionWithSets(id: Long): Flow<SessionWithSets>

    @Query("SELECT * FROM workout_session WHERE sessionId = :id")
    suspend fun getSessionById(id: Long): Session?

    @Update suspend fun updateSession(session: Session)

    // Limit 1 is kinda just a safety measure, as it's always either gonna return
    // 1 value or 0 (null) if my implementation is actually done right
    @Query("SELECT * FROM workout_session WHERE endTimestamp IS NULL LIMIT 1")
    fun getActiveSessionWithSets(): Flow<SessionWithSets?>

    @Query(
        "SELECT COUNT(*) FROM workout_set WHERE sessionId = :sessionId AND exerciseName = :exerciseName"
    )
    suspend fun getLastSetNumberForExercise(sessionId: Long, exerciseName: String): Int

    @Query("SELECT IFFNULL(MAX(exerciseOrder), 0) FROM workout_set WHERE sessionId = :sessionId")
    suspend fun getLastExerciseNumber(sessionId: Long): Int
}
