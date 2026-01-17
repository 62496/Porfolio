package com.example.he2bproject.network.trivia

import com.squareup.moshi.Json


/**
 * Data Transfer Object (DTO) representing a single question from the Open Trivia API.
 * Handles the mapping between the JSON field names (like "correct_answer")
 * and the Kotlin properties.
 */
data class TriviaQuestionDto(
    val question: String,
    @Json(name = "correct_answer") val correctAnswer: String,
    @Json(name = "incorrect_answers") val incorrectAnswers: List<String>
)