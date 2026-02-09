package com.demodogo.ev_sum_2.data


data class User(
    val email: String,
    val password: String
)

object UserStore {
    private const val MAX_USERS = 5
    private var loggedInEmail: String? = null

    fun currentUserEmail(): String? = loggedInEmail
    private val registeredUsers = mutableListOf<User>()
    private fun allUsers(): List<User> = registeredUsers + testUser

    private val testUser = User(email = "admin@test.cl", password = "1234")

    fun canRegister(email: String): Boolean {
        val cleanEmail = email.trim()
        if (registeredUsers.size >= MAX_USERS) return false
        return allUsers().none { it.email.equals(cleanEmail, ignoreCase = true) }
    }

    fun register(email: String, password: String): Boolean {
        val cleanEmail = email.trim()
        if (cleanEmail.isBlank() || password.isBlank()) return false
        if (!canRegister(cleanEmail)) return false

        registeredUsers.add(User(cleanEmail, password))
        return true
    }

    fun login(email: String, password: String): Boolean {
        val cleanEmail = email.trim()
        val ok = allUsers().any {
            it.email.equals(cleanEmail, ignoreCase = true) && it.password == password
        }
        loggedInEmail = if (ok) cleanEmail else null
        return ok
    }

    fun logout() { loggedInEmail = null }

    fun registeredCount(): Int = registeredUsers.size

}