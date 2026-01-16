package com.example.kalkulatorbungamajemuk.data.repository

import com.example.kalkulatorbungamajemuk.data.api.RetrofitClient
import com.example.kalkulatorbungamajemuk.data.model.*

class HistoryRepository {

    private val api = RetrofitClient.apiService

    suspend fun getHistory(token: String): Result<List<History>> {
        return try {
            val response = api.getHistory("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Failed to load history"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveHistory(
        token: String,
        tipeId: Int,
        keterangan: String?,
        modalAwal: Double,
        bunga: Double,
        waktu: Int,
        saldoAkhir: Double
    ): Result<String> {
        return try {
            val response = api.saveHistory(
                "Bearer $token",
                SaveHistoryRequest(tipeId, keterangan, modalAwal, bunga, waktu, saldoAkhir)
            )
            if (response.isSuccessful) {
                Result.success("History saved successfully")
            } else {
                Result.failure(Exception("Failed to save history"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateHistory(
        token: String,
        id: Int,
        tipeId: Int,
        keterangan: String?,
        modalAwal: Double,
        bunga: Double,
        waktu: Int,
        saldoAkhir: Double
    ): Result<String> {
        return try {
            val response = api.updateHistory(
                "Bearer $token",
                id,
                SaveHistoryRequest(tipeId, keterangan, modalAwal, bunga, waktu, saldoAkhir)
            )
            if (response.isSuccessful) {
                Result.success("History updated successfully")
            } else {
                Result.failure(Exception("Failed to update history"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteHistory(token: String, id: Int): Result<String> {
        return try {
            val response = api.deleteHistory("Bearer $token", id)
            if (response.isSuccessful) {
                Result.success("History deleted successfully")
            } else {
                Result.failure(Exception("Failed to delete history"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getInvestmentTypes(token: String): Result<List<InvestmentType>> {
        return try {
            val response = api.getInvestmentTypes("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Failed to load investment types"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}