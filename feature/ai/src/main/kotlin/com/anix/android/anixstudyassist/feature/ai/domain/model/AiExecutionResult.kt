package com.anix.android.anixstudyassist.feature.ai.domain.model

sealed interface AiExecutionResult {
    data class Success(
        val taskLabel: String,
        val output: String
    ) : AiExecutionResult

    data class Error(
        val reason: String
    ) : AiExecutionResult
}
