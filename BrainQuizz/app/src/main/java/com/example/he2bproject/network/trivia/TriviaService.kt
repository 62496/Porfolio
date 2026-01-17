package com.example.he2bproject.network.trivia

import com.example.he2bproject.data.Constants
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory



/**
 * Singleton object that sets up the Retrofit client for the Open Trivia Database API.
 * It uses a Moshi converter to handle JSON serialization and deserialization
 * for fetching random trivia questions.
 */
object TriviaService {

    private const val BASEURL = Constants.TRIVIA_BASE_URL

    val triviaClient: TriviaHTTPCLIENT

    init {
        // JSON → Kotlin
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        val jsonConverter = MoshiConverterFactory.create(moshi)

        // Retrofit builder
        val retrofitBuilder: Retrofit.Builder = Retrofit.Builder()
            .baseUrl(BASEURL)
            .addConverterFactory(jsonConverter)

        // Retrofit instance
        val retrofit: Retrofit = retrofitBuilder.build()

        // Client
        triviaClient = retrofit.create(TriviaHTTPCLIENT::class.java)
    }
}