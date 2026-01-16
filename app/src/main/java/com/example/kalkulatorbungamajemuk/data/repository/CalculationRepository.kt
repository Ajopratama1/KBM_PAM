package com.example.kalkulatorbungamajemuk.data.repository

import com.example.kalkulatorbungamajemuk.data.api.RetrofitClient
import com.example.kalkulatorbungamajemuk.data.model.*

class CalculationRepository {

    private val api = RetrofitClient.apiService

    suspend fun calculate(token: String, modalAwal: Double, bunga: Double, waktu: Int): Result<CalculationData> {
        return try {
            val response = api.calculate("Bearer $token", CalculationRequest(modalAwal, bunga, waktu))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Calculation failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
