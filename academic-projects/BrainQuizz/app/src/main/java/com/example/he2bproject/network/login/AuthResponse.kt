package com.example.he2bproject.network.login

/**
 * Data class representing the response received from the Authentication API upon successful login.
 * Contains the JWT access token and its expiration details.
 */
data class AuthResponse(
    val access_token: String?,
    val token_type: String?,
    val expires_in: Int?,
    val expires_at: Long?,
    val refresh_token: String?
)
