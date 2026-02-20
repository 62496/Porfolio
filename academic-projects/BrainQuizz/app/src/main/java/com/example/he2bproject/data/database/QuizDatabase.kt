package com.example.he2bproject.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.he2bproject.data.Constants
import com.example.he2bproject.data.model.HistoryEntity
import com.example.he2bproject.data.model.QuestionEntity
import com.example.he2bproject.data.model.QuizEntity


/**

 * Application Room database.

 * Contains local quizzes, questions, and game history.

 */
@Database(
    entities = [
        QuizEntity::class,
        QuestionEntity::class,
        HistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class QuizDatabase : RoomDatabase() {

    abstract fun quizDao(): QuizDao
    abstract fun historyDao(): HistoryDao

    companion object {

        private const val DATABASE_NAME = Constants.DATABASE_NAME

        @Volatile
        private var INSTANCE: QuizDatabase? = null

        fun getInstance(context: Context): QuizDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuizDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}