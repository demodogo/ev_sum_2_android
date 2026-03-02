package com.demodogo.ev_sum_2.domain.validators

import org.junit.Assert.assertEquals
import org.junit.Test

class SpeechNormalizersTest {

    @Test
    // @Test: Debería normalizar los simbolos y eliminar los espacios en blanco
    fun `normalizeEmailFromSpeech should normalize spoken symbols and remove spaces`() {
        val input = "maca i latina g m arroba g mail punto com"
        val expected = "macaigm@gmail.com"

        assertEquals(expected, normalizeEmailFromSpeech(input))
    }

    @Test
    // @Test: Debería eliminar los espacios y normalizar nombres de números (ej: uno, dos, tres, etc) y nombres de letras
    fun `normalizePasswordFromSpeech should join digits and map letter names`() {
        val input = "maca i latina uno 2 tres"
        val expected = "macai123"

        assertEquals(expected, normalizePasswordFromSpeech(input))
    }
}