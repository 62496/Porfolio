package com.example.he2bproject.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.he2bproject.data.model.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity)

    /**

     * Retrieves the game history, from the most recent to the oldest.

     */
    @Query("SELECT * FROM history ORDER BY playedAt DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    /**

     * Adds a new entry to the history.

     */
    @Query("DELETE FROM history")
    suspend fun clearHistory()



}