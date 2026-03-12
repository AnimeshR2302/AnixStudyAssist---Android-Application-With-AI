package com.anix.android.anixstudyassist.feature.auth.domain.repo

interface AuthRepository {
    suspend fun login(userId: String, password: String): Boolean
}
