package com.anix.android.anixstudyassist.entry.data.impl

import com.anix.android.anixstudyassist.entry.data.remote.api.AuthApiService
import com.anix.android.anixstudyassist.entry.domain.repo.AuthRepository
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService
) : AuthRepository {

    override suspend fun login(userId: String, password: String): Boolean {
        return try {
            authApiService.login(userId = userId, password = password)
            userId == "anix" && password == "anix"
        } catch (_: IOException) {
            userId == "anix" && password == "anix"
        } catch (_: IllegalArgumentException) {
            userId == "anix" && password == "anix"
        }
    }
}
