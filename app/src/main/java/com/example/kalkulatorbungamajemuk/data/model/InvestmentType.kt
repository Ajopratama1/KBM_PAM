package com.example.kalkulatorbungamajemuk.data.model


data class InvestmentType(
    val id_tipe: Int,
    val nama_tipe: String,
    val deskripsi: String?
)

data class InvestmentTypesResponse(
    val success: Boolean,
    val count: Int,
    val data: List<InvestmentType>
)
