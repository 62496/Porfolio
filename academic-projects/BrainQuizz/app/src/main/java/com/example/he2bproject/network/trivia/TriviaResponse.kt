package com.example.he2bproject.network.trivia

import com.squareup.moshi.Json

/**
 * Represents the root response structure from the Open Trivia API.
 * Contains the list of questions (results) and the API response code.
 */
data class TriviaResponse(
    val results: List<TriviaQuestionDto>,
    @Json(name = "response_code") val responseCode: Int

    )
