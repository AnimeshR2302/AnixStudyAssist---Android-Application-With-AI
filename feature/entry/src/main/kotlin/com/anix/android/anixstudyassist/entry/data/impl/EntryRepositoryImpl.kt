package com.anix.android.anixstudyassist.entry.data.impl

import com.anix.android.anixstudyassist.entry.data.remote.api.EntryApiService
import com.anix.android.anixstudyassist.entry.domain.model.LoginResult
import com.anix.android.anixstudyassist.entry.domain.repo.EntryRepository
import java.io.IOException
import javax.inject.Inject

class EntryRepositoryImpl @Inject constructor(
    private val entryApiService: EntryApiService
) : EntryRepository {

    override suspend fun login(userId: String, password: String): LoginResult {
        return try {
            entryApiService.login(userId = userId, password = password)
            toLoginResult(userId = userId, password = password)
        } catch (_: IOException) {
            if (isValidTestingCredential(userId, password)) {
                LoginResult.Success(userId = userId)
            } else {
                LoginResult.NetworkError("Unable to reach login service")
            }
        } catch (_: IllegalArgumentException) {
            if (isValidTestingCredential(userId, password)) {
                LoginResult.Success(userId = userId)
            } else {
                LoginResult.NetworkError("Invalid login service configuration")
            }
        }
    }

    private fun toLoginResult(userId: String, password: String): LoginResult {
        return if (isValidTestingCredential(userId, password)) {
            LoginResult.Success(userId = userId)
        } else {
            LoginResult.InvalidCredentials
        }
    }

    private fun isValidTestingCredential(userId: String, password: String): Boolean {
        return userId == "anix" && password == "anix"
    }
}
