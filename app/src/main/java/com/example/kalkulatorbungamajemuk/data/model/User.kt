// File: data/model/User.kt
package com.example.kalkulatorbungamajemuk.data.model

data class User(
    val id_pengguna: Int,
    val username: String,
    val nama_pengguna: String
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val password: String,
    val nama_pengguna: String
)

data class AuthResponse(  // ← INI DIA!
    val message: String,
    val token: String? = null,
    val user: User? = null,
    val error: String? = null
)