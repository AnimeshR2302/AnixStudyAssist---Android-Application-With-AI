package com.anix.android.anixstudyassist.entry.domain.repo

import com.anix.android.anixstudyassist.entry.domain.model.LoginResult

interface EntryRepository {
    suspend fun login(userId: String, password: String): LoginResult
}
