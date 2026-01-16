package com.example.kalkulatorbungamajemuk.data.api

import com.example.kalkulatorbungamajemuk.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/calculate")
    suspend fun calculate(
        @Header("Authorization") token: String,
        @Body request: CalculationRequest
    ): Response<CalculationResponse>

    @GET("api/history")
    suspend fun getHistory(
        @Header("Authorization") token: String
    ): Response<HistoryResponse>

    @POST("api/history")
    suspend fun saveHistory(
        @Header("Authorization") token: String,
        @Body request: SaveHistoryRequest
    ): Response<Map<String, Any>>

    @PUT("api/history/{id}")
    suspend fun updateHistory(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: SaveHistoryRequest
    ): Response<Map<String, String>>

    @DELETE("api/history/{id}")
    suspend fun deleteHistory(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Map<String, String>>

    @GET("api/investment-types")
    suspend fun getInvestmentTypes(
        @Header("Authorization") token: String
    ): Response<InvestmentTypesResponse>
}
