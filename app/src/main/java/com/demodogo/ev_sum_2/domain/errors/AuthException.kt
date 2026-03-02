package com.demodogo.ev_sum_2.domain.errors

class AuthException(message: String, cause: Throwable? = null) : AppException(message, cause)