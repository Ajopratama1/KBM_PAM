package com.example.kalkulatorbungamajemuk.data.model

data class History(
    val id_riwayat: Int,
    val keterangan: String?,
    val modal_awal_p: Double,
    val bunga_r: Double,
    val waktu_t: Int,
    val saldo_akhir_a: Double,
    val tgl_simpan: String,
    val id_tipe: Int,
    val nama_tipe: String,
    val username: String,
    val nama_pengguna: String
)

data class HistoryResponse(
    val success: Boolean,
    val count: Int,
    val data: List<History>
)

data class SaveHistoryRequest(
    val id_tipe: Int,
    val keterangan: String?,
    val modal_awal_p: Double,
    val bunga_r: Double,
    val waktu_t: Int,
    val saldo_akhir_a: Double
)

data class CalculationRequest(
    val modal_awal_p: Double,
    val bunga_r: Double,
    val waktu_t: Int
)

data class CalculationData(
    val modal_awal_p: Double,
    val bunga_r: Double,
    val waktu_t: Int,
    val saldo_akhir_a: Double,
    val total_bunga: Double,
    val formula: String
)

data class CalculationResponse(
    val success: Boolean,
    val data: CalculationData
)