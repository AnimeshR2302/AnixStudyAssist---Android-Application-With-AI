package com.anix.android.anixstudyassist.aikit.domain.model

sealed interface OnlineAiResult {
    data class Success(
        val output: String,
        val usedSearchGrounding: Boolean = false
    ) : OnlineAiResult

    data class Error(
        val reason: String,
        val diagnosticDetails: String? = null
    ) : OnlineAiResult
}
