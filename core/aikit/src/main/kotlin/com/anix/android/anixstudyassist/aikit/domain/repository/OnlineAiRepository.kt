package com.anix.android.anixstudyassist.aikit.domain.repository

import com.anix.android.anixstudyassist.aikit.domain.model.OnlineAiResult

interface OnlineAiRepository {
    suspend fun getConversationalResponse(prompt: String): OnlineAiResult
    suspend fun getSearchResponse(query: String): OnlineAiResult
}
