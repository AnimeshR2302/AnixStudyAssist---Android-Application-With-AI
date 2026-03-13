package com.anix.android.anixstudyassist.feature.ai.data.repository

import android.content.Context
import com.anix.android.anixstudyassist.feature.ai.domain.model.AiExecutionResult
import com.anix.android.anixstudyassist.feature.ai.domain.model.AiTask
import com.anix.android.anixstudyassist.feature.ai.domain.model.RewriteTone
import com.anix.android.anixstudyassist.feature.ai.domain.repository.OnDeviceAiRepository
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.proofreading.ProofreaderOptions
import com.google.mlkit.genai.proofreading.Proofreading
import com.google.mlkit.genai.proofreading.ProofreadingRequest
import com.google.mlkit.genai.rewriting.RewriterOptions
import com.google.mlkit.genai.rewriting.Rewriting
import com.google.mlkit.genai.rewriting.RewritingRequest
import com.google.mlkit.genai.summarization.Summarization
import com.google.mlkit.genai.summarization.SummarizationRequest
import com.google.mlkit.genai.summarization.SummarizerOptions
import kotlinx.coroutines.guava.await
import javax.inject.Inject

class OnDeviceAiRepositoryImpl @Inject constructor(
    private val context: Context
) : OnDeviceAiRepository {

    override suspend fun execute(task: AiTask): AiExecutionResult {
        return try {
            when (task) {
                is AiTask.Summarize -> runSummarization(task)
                is AiTask.Proofread -> runProofreading(task)
                is AiTask.Rewrite -> runRewriting(task)
            }
        } catch (error: Throwable) {
            AiExecutionResult.Error(error.message ?: "Unknown on-device AI error.")
        }
    }

    private suspend fun runSummarization(task: AiTask.Summarize): AiExecutionResult {
        val options = SummarizerOptions.builder(context)
            .setLanguage(SummarizerOptions.Language.ENGLISH)
            .setInputType(SummarizerOptions.InputType.ARTICLE)
            .setOutputType(SummarizerOptions.OutputType.THREE_BULLETS)
            .setLongInputAutoTruncationEnabled(true)
            .build()

        val client = Summarization.getClient(options)
        return try {
            when (val status = client.checkFeatureStatus().await()) {
                FeatureStatus.AVAILABLE -> {
                    val request = SummarizationRequest.builder(task.text).build()
                    val result = client.runInference(request).await()
                    AiExecutionResult.Success(
                        taskLabel = "Summarization",
                        output = result.summary
                    )
                }

                else -> AiExecutionResult.Error(featureStatusError("Summarization", status))
            }
        } finally {
            client.close()
        }
    }

    private suspend fun runProofreading(task: AiTask.Proofread): AiExecutionResult {
        val options = ProofreaderOptions.builder(context)
            .setLanguage(ProofreaderOptions.Language.ENGLISH)
            .setInputType(ProofreaderOptions.InputType.KEYBOARD)
            .build()

        val client = Proofreading.getClient(options)
        return try {
            when (val status = client.checkFeatureStatus().await()) {
                FeatureStatus.AVAILABLE -> {
                    val request = ProofreadingRequest.builder(task.text).build()
                    val result = client.runInference(request).await()
                    val output = result.results.firstOrNull()?.text
                        ?: "No proofreading suggestions were returned."
                    AiExecutionResult.Success(
                        taskLabel = "Proofreading",
                        output = output
                    )
                }

                else -> AiExecutionResult.Error(featureStatusError("Proofreading", status))
            }
        } finally {
            client.close()
        }
    }

    private suspend fun runRewriting(task: AiTask.Rewrite): AiExecutionResult {
        val outputType = when (task.tone) {
            RewriteTone.FRIENDLY -> RewriterOptions.OutputType.FRIENDLY
            RewriteTone.PROFESSIONAL -> RewriterOptions.OutputType.PROFESSIONAL
            RewriteTone.SHORTEN -> RewriterOptions.OutputType.SHORTEN
            RewriteTone.REPHRASE -> RewriterOptions.OutputType.REPHRASE
            RewriteTone.ELABORATE -> RewriterOptions.OutputType.ELABORATE
            RewriteTone.EMOJIFY -> RewriterOptions.OutputType.EMOJIFY
        }

        val options = RewriterOptions.builder(context)
            .setLanguage(RewriterOptions.Language.ENGLISH)
            .setOutputType(outputType)
            .build()

        val client = Rewriting.getClient(options)
        return try {
            when (val status = client.checkFeatureStatus().await()) {
                FeatureStatus.AVAILABLE -> {
                    val request = RewritingRequest.builder(task.text).build()
                    val result = client.runInference(request).await()
                    val output = result.results.firstOrNull()?.text
                        ?: "No rewriting suggestions were returned."
                    AiExecutionResult.Success(
                        taskLabel = "Rewriting (${task.tone.name.lowercase()})",
                        output = output
                    )
                }

                else -> AiExecutionResult.Error(featureStatusError("Rewriting", status))
            }
        } finally {
            client.close()
        }
    }

    private fun featureStatusError(task: String, status: Int): String {
        val reason = when (status) {
            FeatureStatus.UNAVAILABLE -> "feature is unavailable on this device"
            FeatureStatus.DOWNLOADABLE -> "required on-device model is not downloaded"
            FeatureStatus.DOWNLOADING -> "required on-device model is downloading"
            else -> "unknown feature state: $status"
        }
        return "$task failed: $reason."
    }
}
