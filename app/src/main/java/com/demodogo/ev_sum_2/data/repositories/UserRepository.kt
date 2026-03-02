package com.demodogo.ev_sum_2.data.repositories

import com.demodogo.ev_sum_2.data.firebase.FirebaseModule
import com.demodogo.ev_sum_2.domain.errors.DataException
import com.demodogo.ev_sum_2.domain.models.AppUser
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val col = FirebaseModule.db.collection("users");

    suspend fun upsertUser(user: AppUser) {
        try {
            col.document(user.uid).set(user).await();
        } catch(e: Exception) {
            throw DataException("Fallo al actualizar el usuario: ", e)
        }
    }

    suspend fun getUser(uid: String): AppUser? {
        return try {
            val doc = col.document(uid).get().await()
            doc.toObject(AppUser::class.java)
        } catch (e: Exception) {
            throw DataException("Fallo al obtener el usuario: ", e)
        }
    }
}