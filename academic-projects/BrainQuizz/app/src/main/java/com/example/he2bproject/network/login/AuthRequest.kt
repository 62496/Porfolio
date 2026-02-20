package com.example.he2bproject.network.login

/**
 * Data class representing the login credentials sent to the API.
 * @property email The user's email address.
 * @property password The user's password.
 */
data class AuthRequest(
    val email : String,
    val password : String
)
