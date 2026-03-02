package com.demodogo.ev_sum_2.services

import com.demodogo.ev_sum_2.data.repositories.PhraseRepository
import com.demodogo.ev_sum_2.domain.models.Phrase

class PhraseService (
    private val repo: PhraseRepository = PhraseRepository()
) {
    suspend fun add (text: String) {
        try {
            val clean = text.trim()
            require(clean.isNotBlank()) { "La frase no puede estar vacía" }
            repo.add(clean)
        } catch(e: Exception) {
            throw Exception(e.message ?: "Error al guardar la frase")
        }
    }

    suspend fun get(): List<Phrase> {
        try {
            return repo.get()
        } catch(e: Exception) {
            throw Exception(e.message ?: "Error al cargar las frases")
        }
    }

    suspend fun update(id: String, newText: String) {
        val clean = newText.trim()
        if (clean.isBlank()) throw IllegalArgumentException("La frase no puede estar vacía")
        repo.update(id, clean);
    }
    suspend fun delete(id: String) {
        try {
            require(id.isNotBlank()) { "Id inválido" }
            repo.delete(id);
        } catch(e: Exception) {
            throw Exception(e.message ?: "Error al eliminar la frase")
        }
    }
}