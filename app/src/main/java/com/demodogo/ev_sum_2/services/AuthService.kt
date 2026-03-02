package com.demodogo.ev_sum_2.services

import com.demodogo.ev_sum_2.data.repositories.AuthRepository
import com.demodogo.ev_sum_2.data.repositories.UserRepository
import com.demodogo.ev_sum_2.domain.models.AppUser
import com.demodogo.ev_sum_2.domain.validators.isBasicEmailValid
import com.demodogo.ev_sum_2.domain.validators.isValidPassword

class AuthService(
    private val repo: AuthRepository = AuthRepository(),
) {

    suspend fun login(email: String, password: String) {
        try {
            repo.login(email, password)
        } catch (e: Exception) {
            throw mapFirebaseAuthError(e)
        }
    }

    suspend fun register(email: String, password: String) {
        try {
            repo.register(email, password)
        } catch (e: Exception) {
            throw mapFirebaseAuthError(e)
        }
    }

    suspend fun recover(email: String) {
        try {
            repo.sendPasswordReset(email)
        } catch (e: Exception) {
            throw mapFirebaseAuthError(e)
        }
    }

    fun logout() = repo.logout()
    fun isLoggedIn(): Boolean = repo.isLoggedIn()
    fun currentEmail(): String? = repo.currentEmail()

    private fun mapFirebaseAuthError(e: Exception): Exception {
        val msg = e.message ?: "Error de autenticación"
        return Exception(msg)
    }
}