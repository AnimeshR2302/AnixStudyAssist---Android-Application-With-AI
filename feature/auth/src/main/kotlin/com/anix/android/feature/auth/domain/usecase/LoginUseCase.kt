package com.anix.android.anixstudyassist.feature.auth.domain.usecase

import com.anix.android.anixstudyassist.feature.auth.domain.repo.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(userId: String, password: String): Boolean {
        return repository.login(userId, password)
    }
}
