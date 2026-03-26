package com.anix.android.anixstudyassist.entry.domain.usecase

import com.anix.android.anixstudyassist.entry.domain.model.LoginResult
import com.anix.android.anixstudyassist.entry.domain.repo.EntryRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: EntryRepository
) {
    suspend operator fun invoke(userId: String, password: String): LoginResult {
        return repository.login(userId, password)
    }
}
