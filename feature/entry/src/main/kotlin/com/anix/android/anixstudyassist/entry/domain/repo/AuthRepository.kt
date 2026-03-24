package com.anix.android.anixstudyassist.entry.domain.repo

interface AuthRepository {
    suspend fun login(userId: String, password: String): Boolean
}
