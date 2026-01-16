package com.example.kalkulatorbungamajemuk.data.repository

import com.example.kalkulatorbungamajemuk.data.api.RetrofitClient
import com.example.kalkulatorbungamajemuk.data.model.*

class AuthRepository {

    private val api = RetrofitClient.apiService

    suspend fun register(username: String, password: String, namaLengkap: String): Result<AuthResponse> {
        return try {
            val response = api.register(RegisterRequest(username, password, namaLengkap))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(username: String, password: String): Result<AuthResponse> {
        return try {
            val response = api.login(LoginRequest(username, password))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Invalid credentials"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}