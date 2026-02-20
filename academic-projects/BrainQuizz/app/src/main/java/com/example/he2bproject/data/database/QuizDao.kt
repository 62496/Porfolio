package com.example.he2bproject.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.he2bproject.data.model.QuestionEntity
import com.example.he2bproject.data.model.QuizEntity

import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuiz(quiz: QuizEntity): Long

    @Update
    suspend fun updateQuiz(quiz: QuizEntity)

    @Query("SELECT * FROM quizzes")
    fun getAllQuizzes(): Flow<List<QuizEntity>>

    @Query("""
        SELECT * FROM quizzes
        WHERE (:category IS NULL OR category = :category)
        AND (:difficulty IS NULL OR difficulty = :difficulty)
        AND (:onlyFavorites = 0 OR isFavorite = 1)
        ORDER BY title
    """)
    fun getFilteredQuizzes(
        category: String?,
        difficulty: String?,
        onlyFavorites: Boolean
    ): Flow<List<QuizEntity>>

    @Query("SELECT * FROM quizzes WHERE isFavorite = 1")
    fun getFavoriteQuizzes(): Flow<List<QuizEntity>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Query("SELECT * FROM questions WHERE quizId = :quizId")
    suspend fun getQuestionsForQuiz(quizId: Long): List<QuestionEntity>

    @Query("SELECT COUNT(*) FROM quizzes")
    suspend fun getQuizCount(): Int

    @Query("SELECT * FROM quizzes WHERE id = :id")
    suspend fun getQuizById(id: Long): QuizEntity?

}