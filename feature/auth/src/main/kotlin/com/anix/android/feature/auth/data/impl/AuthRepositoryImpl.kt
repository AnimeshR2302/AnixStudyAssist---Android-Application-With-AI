package com.anix.android.anixstudyassist.feature.auth.data.impl

import com.anix.android.anixstudyassist.feature.auth.data.remote.api.AuthApiService
import com.anix.android.anixstudyassist.feature.auth.domain.repo.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService
) : AuthRepository {

    override suspend fun login(userId: String, password: String): Boolean {
        // TODO: Replace with retrofit call via authApiService once backend API is ready.
        return userId == "anix" && password == "anix"
    }
}
