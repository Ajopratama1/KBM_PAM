package com.example.kalkulatorbungamajemuk.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalkulatorbungamajemuk.data.local.SessionManager
import com.example.kalkulatorbungamajemuk.data.model.History
import com.example.kalkulatorbungamajemuk.data.model.InvestmentType
import com.example.kalkulatorbungamajemuk.data.repository.HistoryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HistoryRepository()
    private val sessionManager = SessionManager(application)

    private val _historyState = MutableStateFlow<HistoryState>(HistoryState.Loading)
    val historyState: StateFlow<HistoryState> = _historyState.asStateFlow()

    private val _investmentTypes = MutableStateFlow<List<InvestmentType>>(emptyList())
    val investmentTypes: StateFlow<List<InvestmentType>> = _investmentTypes.asStateFlow()

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    init {
        loadHistory()
        loadInvestmentTypes()
    }

    fun loadHistory() {
        viewModelScope.launch {
            sessionManager.token.first()?.let { token ->
                _historyState.value = HistoryState.Loading
                val result = repository.getHistory(token)
                result.onSuccess { histories ->
                    _historyState.value = HistoryState.Success(histories)
                }.onFailure { error ->
                    _historyState.value = HistoryState.Error(error.message ?: "Failed to load")
                }
            }
        }
    }

    private fun loadInvestmentTypes() {
        viewModelScope.launch {
            sessionManager.token.first()?.let { token ->
                val result = repository.getInvestmentTypes(token)
                result.onSuccess { types ->
                    _investmentTypes.value = types
                }
            }
        }
    }

    fun saveHistory(tipeId: Int, keterangan: String?, modalAwal: Double, bunga: Double, waktu: Int, saldoAkhir: Double) {
        viewModelScope.launch {
            sessionManager.token.first()?.let { token ->
                _saveState.value = SaveState.Loading
                val result = repository.saveHistory(token, tipeId, keterangan, modalAwal, bunga, waktu, saldoAkhir)
                result.onSuccess {
                    _saveState.value = SaveState.Success
                    loadHistory() // Refresh list
                }.onFailure { error ->
                    _saveState.value = SaveState.Error(error.message ?: "Save failed")
                }
            }
        }
    }

    fun updateHistory(
        id: Int,
        tipeId: Int,
        keterangan: String?,
        modalAwal: Double,
        bunga: Double,
        waktu: Int,
        saldoAkhir: Double
    ) {
        viewModelScope.launch {
            sessionManager.token.first()?.let { token ->
                _saveState.value = SaveState.Loading
                val result = repository.updateHistory(token, id, tipeId, keterangan, modalAwal, bunga, waktu, saldoAkhir)
                result.onSuccess {
                    _saveState.value = SaveState.Success
                    loadHistory() // Refresh list
                }.onFailure { error ->
                    _saveState.value = SaveState.Error(error.message ?: "Update failed")
                }
            }
        }
    }

    fun deleteHistory(id: Int) {
        viewModelScope.launch {
            sessionManager.token.first()?.let { token ->
                repository.deleteHistory(token, id)
                loadHistory() // Refresh list
            }
        }
    }

    fun resetSaveState() {
        _saveState.value = SaveState.Idle
    }
}

sealed class HistoryState {
    object Loading : HistoryState()
    data class Success(val histories: List<History>) : HistoryState()
    data class Error(val message: String) : HistoryState()
}

sealed class SaveState {
    object Idle : SaveState()
    object Loading : SaveState()
    object Success : SaveState()
    data class Error(val message: String) : SaveState()
}
