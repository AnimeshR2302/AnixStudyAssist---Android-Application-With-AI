package com.anix.android.anixstudyassist.entry.domain.model

sealed interface LoginResult {
    data class Success(val userId: String) : LoginResult
    data object InvalidCredentials : LoginResult
    data class NetworkError(val message: String) : LoginResult
}

data class AuthSuccessData(
    val userId: String
)
