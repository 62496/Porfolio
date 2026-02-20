package com.example.he2bproject.data.repository

import android.content.Context
import android.content.res.AssetManager
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.he2bproject.data.database.Converters
import com.example.he2bproject.data.database.QuizDatabase
import com.example.he2bproject.data.model.HistoryEntity
import com.example.he2bproject.data.model.QuestionEntity
import com.example.he2bproject.data.model.QuizEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

import com.example.he2bproject.data.Constants

private val Context.dataStore by preferencesDataStore(name = Constants.GAME_PREFS_NAME)

/**
 * Singleton Repository handling data operations for Quizzes and History.
 * It manages sources from Room Database and DataStore preferences.
 */
object QuizRepository {

    private lateinit var database: QuizDatabase

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var assetManager: AssetManager

    // DataStore Keys
    private val KEY_QUIZ_ID = longPreferencesKey("saved_quiz_id")
    private val KEY_CURRENT_INDEX = intPreferencesKey("saved_current_index")
    private val KEY_SCORE = intPreferencesKey("saved_score")
    private val KEY_TIME_LEFT = intPreferencesKey("saved_time_left")

    /**
     * Initializes the repository with the application context.
     * Should be called in the Application class or MainActivity onCreate.
     */
    fun initDatabase(context: Context) {
        val appContext = context.applicationContext
        database = QuizDatabase.getInstance(appContext)
        dataStore = appContext.dataStore
        assetManager = appContext.assets
    }

    /**
     * Saves the current game state to DataStore.
     */
    suspend fun saveGameSnapshot(quizId: Long, currentIndex: Int, score: Int, timeLeft: Int) {
        dataStore.edit { prefs ->
            prefs[KEY_QUIZ_ID] = quizId
            prefs[KEY_CURRENT_INDEX] = currentIndex
            prefs[KEY_SCORE] = score
            prefs[KEY_TIME_LEFT] = timeLeft
        }
    }

    /**
     * Clears any saved game state from DataStore.
     */
    suspend fun clearGameSnapshot() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }

    /**
     * Data class representing a saved game state.
     */
    data class GameSnapshot(
        val quizId: Long,
        val currentIndex: Int,
        val score: Int,
        val timeLeft: Int
    )

    /**
     * Retrieves the saved game snapshot once (synchronous-like).
     * Returns null if no valid game is saved.
     */
    suspend fun getGameSnapshot(): GameSnapshot? {
        val prefs = dataStore.data.first()
        val quizId = prefs[KEY_QUIZ_ID] ?: -1L
        if (quizId == -1L) return null
        return GameSnapshot(
            quizId = quizId,
            currentIndex = prefs[KEY_CURRENT_INDEX] ?: 0,
            score = prefs[KEY_SCORE] ?: 0,
            timeLeft = prefs[KEY_TIME_LEFT] ?: 0
        )
    }

    /**
     * Observes the saved game snapshot as a Flow.
     * Emits null if no game is saved.
     */
    fun getGameSnapshotFlow(): Flow<GameSnapshot?> {
        return dataStore.data.map { prefs ->
            val quizId = prefs[KEY_QUIZ_ID] ?: -1L
            if (quizId == -1L) null
            else GameSnapshot(
                quizId = quizId,
                currentIndex = prefs[KEY_CURRENT_INDEX] ?: 0,
                score = prefs[KEY_SCORE] ?: 0,
                timeLeft = prefs[KEY_TIME_LEFT] ?: 0
            )
        }
    }

    fun getFavoriteQuizzes(): Flow<List<QuizEntity>> {
        return database.quizDao().getFavoriteQuizzes()
    }
    suspend fun getQuizById(id: Long): QuizEntity? {
        return database.quizDao().getQuizById(id)
    }
    fun getFilteredQuizzes(
        category: String?,
        difficulty: String?,
        onlyFavorites: Boolean
    ): Flow<List<QuizEntity>> {
        return database.quizDao().getFilteredQuizzes(category, difficulty,onlyFavorites)
    }

    suspend fun updateQuiz(quiz: QuizEntity) {
        database.quizDao().updateQuiz(quiz)
    }


    suspend fun getQuestionsForQuiz(quizId: Long): List<QuestionEntity> {
        return database.quizDao().getQuestionsForQuiz(quizId)
    }


    fun getAllHistory(): Flow<List<HistoryEntity>> {
        return database.historyDao().getAllHistory()
    }

    suspend fun insertHistory(history: HistoryEntity) {
        database.historyDao().insertHistory(history)
    }
    /**
     * Populates the database with initial data from "quizzes.json".
     * Executes on the IO dispatcher to prevent UI blocking.
     */
    suspend fun populateDatabaseIfNeeded() {
        val count = database.quizDao().getQuizCount()
        if (count > 0) {
            return
        }
        val jsonString = getJsonDataFromAsset("quizzes.json") ?: return

        // Transform JSON text to Kotlin object list
        val gson = Gson()
        val listType = object : TypeToken<List<Converters.QuizJson>>() {}.type
        val quizzesFromJson: List<Converters.QuizJson> = gson.fromJson(jsonString, listType)

        quizzesFromJson.forEach { quizJson ->
            val quizId = database.quizDao().insertQuiz(
                QuizEntity(
                    title = quizJson.title,
                    category = quizJson.category,
                    difficulty = quizJson.difficulty
                )
            )

            val questionEntities = quizJson.questions.map { qJson ->
                QuestionEntity(
                    quizId = quizId,
                    questionText = qJson.text,
                    answers = qJson.answers.shuffled(),
                    correctAnswer = qJson.correct
                )
            }

            database.quizDao().insertQuestions(questionEntities)
        }
    }

    /**
     * Helper to read a file from the assets folder.
     */
    private fun getJsonDataFromAsset(fileName: String): String? {
        return try {
            assetManager.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            null
        }
    }
}