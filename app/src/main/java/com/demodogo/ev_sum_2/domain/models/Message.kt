package com.demodogo.ev_sum_2.domain.models

data class Message (
    val id: String = "",
    val uid: String = "",
    val text: String = "",
    val createdAtMs: Long =  0L
)