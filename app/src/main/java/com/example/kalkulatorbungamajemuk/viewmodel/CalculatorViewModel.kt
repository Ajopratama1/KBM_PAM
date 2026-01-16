package com.example.kalkulatorbungamajemuk.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalkulatorbungamajemuk.data.local.SessionManager
import com.example.kalkulatorbungamajemuk.data.model.CalculationData
import com.example.kalkulatorbungamajemuk.data.repository.CalculationRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CalculationRepository()
    private val sessionManager = SessionManager(application)

    private val _calculationState = MutableStateFlow<CalculationState>(CalculationState.Idle)
    val calculationState: StateFlow<CalculationState> = _calculationState.asStateFlow()

    fun calculate(modalAwal: Double, bunga: Double, waktu: Int) {
        viewModelScope.launch {
            sessionManager.token.first()?.let { token ->
                _calculationState.value = CalculationState.Loading
                val result = repository.calculate(token, modalAwal, bunga, waktu)
                result.onSuccess { data ->
                    _calculationState.value = CalculationState.Success(data)
                }.onFailure { error ->
                    _calculationState.value = CalculationState.Error(error.message ?: "Calculation failed")
                }
            } ?: run {
                _calculationState.value = CalculationState.Error("Not authenticated")
            }
        }
    }

    fun resetState() {
        _calculationState.value = CalculationState.Idle
    }
}

sealed class CalculationState {
    object Idle : CalculationState()
    object Loading : CalculationState()
    data class Success(val data: CalculationData) : CalculationState()
    data class Error(val message: String) : CalculationState()
}