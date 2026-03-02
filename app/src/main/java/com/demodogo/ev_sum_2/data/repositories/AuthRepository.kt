package com.demodogo.ev_sum_2.data.repositories

import com.demodogo.ev_sum_2.data.firebase.FirebaseModule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthRepository (
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
) {

    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
        auth.currentUser != null

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

    fun authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener {
            fa -> trySend(fa.currentUser).isSuccess
        }
        auth.addAuthStateListener(listener)
        trySend(auth.currentUser)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    fun isLoggedIn(): Boolean = auth.currentUser != null

    fun currentEmail(): String? = auth.currentUser?.email

}