package com.example.he2bproject.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Represents a quiz saved locally in the database.
 */
@Entity(tableName = "quizzes")
data class QuizEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val title: String,
    val category: String,
    val difficulty: String,

    val isFavorite: Boolean = false
)
