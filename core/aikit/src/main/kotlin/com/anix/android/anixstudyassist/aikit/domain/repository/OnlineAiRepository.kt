package com.anix.android.anixstudyassist.aikit.domain.repository

interface OnlineAiRepository {
    suspend fun getConversationalResponse(prompt: String): String
    suspend fun getSearchResponse(query: String): String
}
