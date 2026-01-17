package com.example.he2bproject.network.login

import com.example.he2bproject.data.Constants
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Singleton object that configures and provides the Retrofit client for the Authentication API.
 * It initializes the Moshi converter for JSON parsing and builds the Retrofit instance
 * with the base URL defined in Constants.
 */
object AuthService {

    val authClient : AuthHTTPCLIENT

    init{
        // create a converter JSON -> Kotlin
        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        val jsonConverter = MoshiConverterFactory.create(moshi)

        // create a Retrofit builder
        val retrofitBuilder : Retrofit.Builder = Retrofit.Builder()
            .baseUrl(Constants.AUTH_BASE_URL)
            .addConverterFactory(jsonConverter)

        // create a Retrofit instance
        val retrofit : Retrofit = retrofitBuilder.build()

        // create our client
        authClient = retrofit.create(AuthHTTPCLIENT::class.java)

    }

}