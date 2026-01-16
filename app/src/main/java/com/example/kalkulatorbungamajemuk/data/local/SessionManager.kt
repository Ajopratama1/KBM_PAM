package com.example.kalkulatorbungamajemuk.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class SessionManager(private val context: Context) {

    companion object {
        val TOKEN_KEY = stringPreferencesKey("token")
        val USER_ID_KEY = intPreferencesKey("user_id")
        val USERNAME_KEY = stringPreferencesKey("username")
        val NAMA_PENGGUNA_KEY = stringPreferencesKey("nama_pengguna")
    }

    suspend fun saveSession(token: String, user: com.example.kalkulatorbungamajemuk.data.model.User) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[USER_ID_KEY] = user.id_pengguna
            preferences[USERNAME_KEY] = user.username
            preferences[NAMA_PENGGUNA_KEY] = user.nama_pengguna
        }
    }

    val token: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }

    val userId: Flow<Int?> = context.dataStore.data.map { preferences ->
        preferences[USER_ID_KEY]
    }

    val username: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USERNAME_KEY]
    }

    val namaPengguna: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[NAMA_PENGGUNA_KEY]
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}