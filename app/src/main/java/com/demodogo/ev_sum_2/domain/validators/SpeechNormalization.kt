package com.demodogo.ev_sum_2.domain.validators


fun normalizeEmailFromSpeech(input: String): String {
    var s = input.lowercase();

    val wordMap = mapOf(
        "arroba" to "@",
        "punto" to ".",
        "guion bajo" to "_",
        "guionbajo" to "_",
        "guion" to "-",
        "i latina" to "i",
        "ye" to "y",
        "y griega" to "y"
    )

    wordMap.forEach { (word, replacement) ->
        s = s.replace(word, replacement)
    }

    val numberMap = mapOf(
        "cero" to "0",
        "uno" to "1",
        "dos" to "2",
        "tres" to "3",
        "cuatro" to "4",
        "cinco" to "5",
        "seis" to "6",
        "siete" to "7",
        "ocho" to "8",
        "nueve" to "9"
    )

    numberMap.forEach { (word, digit) ->
        s = s.replace(word, digit)
     }

    val tokens = s.split(" ").filter { it.isNotBlank() }

    val singleCharRatio =
        if (tokens.isEmpty()) 0.0
        else tokens.count { it.length == 1 } / tokens.size.toDouble()

    val isSpellingMode = singleCharRatio >= 0.6


    val normalized = tokens.joinToString("") { token ->
        when {
            token == "y" && isSpellingMode -> "i"
            else -> token
        }
    }

    return normalized
        .replace(" ", "")
        .replace(Regex("[^a-z0-9@._-]"), "")
        .trim()
}

fun normalizePasswordFromSpeech(input: String): String {
    var s = input.lowercase()

    val wordMap = mapOf(
        "arroba" to "@",
        "punto" to ".",
        "guion bajo" to "_",
        "guionbajo" to "_",
        "guion" to "-",
        "i latina" to "i",
        "ye" to "y",
        "y griega" to "y"
    )

    wordMap.forEach { (word, replacement) ->
        s = s.replace(word, replacement)
    }

    val numberMap = mapOf(
        "cero" to "0",
        "uno" to "1",
        "dos" to "2",
        "tres" to "3",
        "cuatro" to "4",
        "cinco" to "5",
        "seis" to "6",
        "siete" to "7",
        "ocho" to "8",
        "nueve" to "9"
    )

    numberMap.forEach { (word, digit) ->
        s = s.replace(word, digit)
    }

    val tokens = s.split(" ").filter { it.isNotBlank() }

    val normalized = tokens.joinToString("") { token ->
        when (token) {
            "y" -> "i"
            else -> token
        }
    }

    return normalized
        .replace(Regex("[^a-z0-9@._-]"), "")
        .trim()
}