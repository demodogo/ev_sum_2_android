package com.demodogo.ev_sum_2.services

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class TextToSpeechController(context: Context) {
    private var tts: TextToSpeech? = null
    private var ready = false

    init {
        tts = TextToSpeech(context) { status ->
            ready = status == TextToSpeech.SUCCESS
            if (ready) {
                tts?.language = Locale.forLanguageTag("es-CL")
            }
        }
    }

    fun speak(text: String) {
        if (!ready) return
        val clean = text.trim()
        if (clean.isBlank()) return
        tts?.speak(clean, TextToSpeech.QUEUE_FLUSH, null, "phrase_tts")
    }

    fun stop() {
        tts?.stop()
    }

    fun destroy() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}