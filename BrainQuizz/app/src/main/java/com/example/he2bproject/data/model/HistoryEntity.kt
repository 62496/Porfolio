package com.example.he2bproject.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


/**

 * History of a game played by the user.

 * Allows you to display past results and filter by date.

 */
@Entity(tableName = "history")
data class HistoryEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val quizTitle: String,
    val category: String,
    val difficulty: String,

    val score: Int,
    val totalQuestions: Int,

    val playedAt: Long
)