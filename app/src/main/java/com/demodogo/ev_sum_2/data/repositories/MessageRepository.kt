package com.demodogo.ev_sum_2.data.repositories

import com.demodogo.ev_sum_2.data.firebase.FirebaseModule
import com.demodogo.ev_sum_2.domain.errors.DataException
import com.demodogo.ev_sum_2.domain.models.Message
import kotlinx.coroutines.tasks.await

class MessageRepository {
    private val col = FirebaseModule.db.collection("messages");

    suspend fun create(message: Message): String {
        try {
            val ref = col.add(message).await();
            return ref.id;
        } catch (e: Exception) {
            throw DataException("Fallo al crear el mensaje: ", e)
        }
    }

    suspend fun listByUser(uid: String, limit: Long = 50): List<Message> {
        try {
            val snap = col.whereEqualTo("uid", uid).orderBy("createdAtMs").limit(limit).get().await()
            return snap.documents.mapNotNull {
                d -> d.toObject(Message::class.java)?.copy(id = d.id)
            }
        } catch(e: Exception) {
            throw DataException("Fallo al obtener los mensajes: ", e)
        }
    }
}