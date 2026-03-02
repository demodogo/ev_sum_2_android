package com.demodogo.ev_sum_2.data.repositories

import com.demodogo.ev_sum_2.data.firebase.FirebaseModule
import com.demodogo.ev_sum_2.domain.errors.AuthException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository (
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    fun currentUser(): FirebaseUser? = FirebaseModule.auth.currentUser;

    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    suspend fun sendPasswordReset(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    fun logout() {
        FirebaseModule.auth.signOut()
    }

    fun isLoggedIn(): Boolean = auth.currentUser != null

    fun currentEmail(): String? = auth.currentUser?.email

}