package com.demodogo.ev_sum_2.services

import com.demodogo.ev_sum_2.data.repositories.MessageRepository
import com.demodogo.ev_sum_2.domain.models.Message

class MessageService (
    private val repo: MessageRepository = MessageRepository()
) {
    suspend fun listFiltered(uid: String, query: String): List<Message> {
        val all = repo.listByUser(uid);
        val filtered = all.filter { it.text.contains(query, ignoreCase = true )}

        val normalized = mutableListOf<Message>()
            filtered.forEach label@{
                msg -> val t = msg.text.trim()
                if (t.isEmpty()) return@label
                normalized.add(msg.copy(text = t))
            }
        return normalized
    }
}