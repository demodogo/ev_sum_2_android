package com.demodogo.ev_sum_2.domain.validators

val String.emailDomain: String
    get() = substringAfter("@", missingDelimiterValue = "")

fun String.isBasicEmailValid(): Boolean =
    contains("@") && contains(".") && emailDomain.isNotBlank()

fun String.isValidPassword(): Boolean =
    length >= 6 && any { it.isLetter() } && any { it.isDigit() } && !contains(" ")