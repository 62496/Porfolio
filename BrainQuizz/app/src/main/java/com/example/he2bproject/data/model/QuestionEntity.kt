package com.example.he2bproject.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


/**

 * Question associated with a local quiz.

 * Used only for local game mode.

 */
@Entity(
    tableName = "questions",
    foreignKeys = [
        ForeignKey(
            entity = QuizEntity::class,
            parentColumns = ["id"],
            childColumns = ["quizId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("quizId")]
)
data class QuestionEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val quizId: Long,

    val questionText: String,
    val answers: List<String>,
    val correctAnswer: String
)