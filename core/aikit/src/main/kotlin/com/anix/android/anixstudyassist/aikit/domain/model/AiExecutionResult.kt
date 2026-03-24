package com.anix.android.anixstudyassist.aikit.domain.model

sealed interface AiExecutionResult {
    data class Success(
        val taskLabel: String,
        val output: String
    ) : AiExecutionResult

    data class Error(
        val reason: String,
        val diagnosticDetails: String? = null
    ) : AiExecutionResult
}
