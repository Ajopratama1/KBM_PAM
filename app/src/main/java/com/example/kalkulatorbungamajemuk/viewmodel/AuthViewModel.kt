package com.example.kalkulatorbungamajemuk.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalkulatorbungamajemuk.data.local.SessionManager
import com.example.kalkulatorbungamajemuk.data.model.AuthResponse
import com.example.kalkulatorbungamajemuk.data.repository.AuthRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository()
    private val sessionManager = SessionManager(application)

    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState: StateFlow<AuthState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<AuthState>(AuthState.Idle)
    val registerState: StateFlow<AuthState> = _registerState.asStateFlow()

    val isLoggedIn: StateFlow<Boolean> = sessionManager.token.map { it != null }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = AuthState.Loading
            val result = repository.login(username, password)
            result.onSuccess { response ->
                if (response.token != null && response.user != null) {
                    sessionManager.saveSession(response.token, response.user)
                    _loginState.value = AuthState.Success(response)
                } else {
                    _loginState.value = AuthState.Error("Login failed")
                }
            }.onFailure { error ->
                _loginState.value = AuthState.Error(error.message ?: "Login failed")
            }
        }
    }

    fun register(username: String, password: String, namaLengkap: String) {
        viewModelScope.launch {
            _registerState.value = AuthState.Loading
            val result = repository.register(username, password, namaLengkap)
            result.onSuccess { response ->
                _registerState.value = AuthState.Success(response)
            }.onFailure { error ->
                _registerState.value = AuthState.Error(error.message ?: "Registration failed")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
        }
    }

    fun resetState() {
        _loginState.value = AuthState.Idle
        _registerState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val response: AuthResponse) : AuthState()
    data class Error(val message: String) : AuthState()
}