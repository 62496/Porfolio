package com.example.he2bproject.data.database

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromList(value: List<String>): String {
        return value.joinToString("|")
    }

    @TypeConverter
    fun toList(value: String): List<String> {
        return value.split("|")
    }

    // Helper classes for JSON Parsing
    data class QuizJson(
        val title: String,
        val category: String,
        val difficulty: String,
        val questions: List<QuestionJson>
    )

    data class QuestionJson(
        val text: String,
        val answers: List<String>,
        val correct: String
    )
}