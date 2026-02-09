package com.demodogo.ev_sum_2.data


object Validators {
    fun isValidEmail(email: String): Boolean {
        val e = email.trim()
        return e.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(e).matches()
    }

    fun isValidPassword(password: String): Boolean {
        if (password.length < 6) return false
        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        return hasLetter && hasDigit
    }
}
