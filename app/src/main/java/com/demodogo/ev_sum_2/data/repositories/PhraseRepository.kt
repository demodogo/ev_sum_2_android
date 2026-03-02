package com.demodogo.ev_sum_2.data.repositories

import com.demodogo.ev_sum_2.domain.models.Phrase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class PhraseRepository (
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private fun uid(): String = auth.currentUser?.uid ?: throw IllegalStateException("No hay sesión activa");

    private fun col() = db.collection("users").document(uid()).collection("phrases");

    suspend fun add(text: String) {
        col().add(
            mapOf(
                "text" to text,
                "createdAt" to System.currentTimeMillis()
            )
        ).await()
    }

    suspend fun get(): List<Phrase> {
        val snap = col().orderBy("createdAt", Query.Direction.DESCENDING).get().await()

        return snap.documents.map {
            d ->
            Phrase(
                id = d.id,
                text = d.getString("text") ?: "",
                createdAtMillis = d.getLong("createdAt") ?: 0L
            )
        }
    }

    suspend fun delete(id: String) {
        col().document(id).delete().await()
    }
}