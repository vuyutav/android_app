package com.schoolminimarket.app.model

data class AuthRequest(
    val email: String? = null,
    val nisn: String? = null,
    val password: String
)

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val role: String
)
