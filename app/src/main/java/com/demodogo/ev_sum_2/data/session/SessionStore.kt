package com.demodogo.ev_sum_2.data.session

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "session")

class SessionStore(private val context: Context) {
    private val KEY_EMAIL = stringPreferencesKey("email")

    val emailFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_EMAIL]
    }

    suspend fun setEmail(email: String) {
        context.dataStore.edit { it[KEY_EMAIL] = email }
    }

    suspend fun clear() {
        context.dataStore.edit { it.remove(KEY_EMAIL) }
    }
}