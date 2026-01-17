package com.example.he2bproject.network.login

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthHTTPCLIENT {
    @POST("auth/v1/token?grant_type=password")
    @Headers("Content-Type: application/json")
    suspend fun login(
        @Header("apikey") apiKey: String,
        @Body body: AuthRequest
    ): Response<AuthResponse>

}
