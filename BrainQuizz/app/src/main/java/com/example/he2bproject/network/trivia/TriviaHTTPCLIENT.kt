package com.example.he2bproject.network.trivia

import retrofit2.http.GET
import retrofit2.http.Query

interface TriviaHTTPCLIENT {

    @GET("api.php")
    suspend fun getQuestions(
        @Query("amount") amount: Int,
        @Query("category") category: Int?,
        @Query("difficulty") difficulty: String?,
        @Query("type") type: String = "multiple"
    ): TriviaResponse
}