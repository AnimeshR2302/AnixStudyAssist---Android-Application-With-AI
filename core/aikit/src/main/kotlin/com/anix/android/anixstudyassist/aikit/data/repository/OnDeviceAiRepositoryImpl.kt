package com.anix.android.anixstudyassist.aikit.data.repository

import android.content.Context
import android.util.Log
import com.anix.android.anixstudyassist.aikit.domain.model.AiExecutionResult
import com.anix.android.anixstudyassist.aikit.domain.model.AiTask
import com.anix.android.anixstudyassist.aikit.domain.model.RewriteTone
import com.anix.android.anixstudyassist.aikit.domain.repository.OnDeviceAiRepository
import com.google.mlkit.genai.common.DownloadCallback
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.common.GenAiException
import com.google.mlkit.genai.proofreading.Proofreader
import com.google.mlkit.genai.proofreading.ProofreaderOptions
import com.google.mlkit.genai.proofreading.Proofreading
import com.google.mlkit.genai.proofreading.ProofreadingRequest
import com.google.mlkit.genai.rewriting.Rewriter
import com.google.mlkit.genai.rewriting.RewriterOptions
import com.google.mlkit.genai.rewriting.Rewriting
import com.google.mlkit.genai.rewriting.RewritingRequest
import com.google.mlkit.genai.summarization.Summarization
import com.google.mlkit.genai.summarization.SummarizationRequest
import com.google.mlkit.genai.summarization.Summarizer
import com.google.mlkit.genai.summarization.SummarizerOptions
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class OnDeviceAiRepositoryImpl @Inject constructor(private val context: Context) :
    OnDeviceAiRepository {

    companion object {
        private const val TAG = "ANIX_AiExecution"
    }

    override suspend fun executeOnDeviceTask(task: AiTask): AiExecutionResult {
        Log.d(TAG, "Executing task=${task.describeForLog()}")
        val startTime = System.currentTimeMillis()
        return try {
            val result = when (task) {
                is AiTask.Summarize -> runSummarization(task)
                is AiTask.Rewrite -> runRewriting(task)
                is AiTask.Proofread -> runProofreading(task)
            }
            val duration = System.currentTimeMillis() - startTime
            Log.d(TAG, "Task ${task.humanLabel()} completed in ${duration}ms")
            result
        } catch (error: Throwable) {
            val duration = System.currentTimeMillis() - startTime
            Log.e(
                TAG,
                "Unhandled task failure for ${task.describeForLog()} after ${duration}ms",
                error
            )
            AiExecutionResult.Error(
                reason = "On-device AI execution crashed before completing.",
                diagnosticDetails = buildDiagnosticDetails(
                    taskName = task.humanLabel(),
                    status = null,
                    detail = "${error::class.java.simpleName}: ${error.message ?: "No message"}"
                )
            )
        }
    }

    private suspend fun runSummarization(task: AiTask.Summarize): AiExecutionResult {
        Log.d(TAG, "Creating summarizer client for textLength=${task.text.length}")

        val summarizerOptions = SummarizerOptions.builder(context).apply {
            setLanguage(SummarizerOptions.Language.ENGLISH)
            setInputType(SummarizerOptions.InputType.ARTICLE)
            setOutputType(SummarizerOptions.OutputType.THREE_BULLETS)
            setLongInputAutoTruncationEnabled(true)
        }.build()
        val client = Summarization.getClient(summarizerOptions)

        return try {
            executeWithFeatureReadiness(
                taskName = "Summarization",
                client = client,
                startInference = {
                    val request = SummarizationRequest.builder(task.text).build()
                    val result = client.runInference(request).await()
                    AiExecutionResult.Success(
                        taskLabel = "Summarization",
                        output = result.summary
                    )
                }
            )
        } finally {
            client.close()
        }
    }

    private suspend fun runProofreading(task: AiTask.Proofread): AiExecutionResult {
        Log.d(TAG, "Creating proofreader client for textLength=${task.text.length}")
        val options = ProofreaderOptions.builder(context)
            .setLanguage(ProofreaderOptions.Language.ENGLISH)
            .setInputType(ProofreaderOptions.InputType.KEYBOARD)
            .build()

        val client = Proofreading.getClient(options)
        return try {
            executeWithFeatureReadiness(
                taskName = "Proofreading",
                client = client,
                startInference = {
                    val request = ProofreadingRequest.builder(task.text).build()
                    val result = client.runInference(request).await()
                    val output = result.results.firstOrNull()?.text
                        ?: "No proofreading suggestions were returned."
                    AiExecutionResult.Success(
                        taskLabel = "Proofreading",
                        output = output
                    )
                }
            )
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
            executeWithFeatureReadiness(
                taskName = "Rewriting (${task.tone.name.lowercase()})",
                client = client,
                startInference = {
                    val request = RewritingRequest.builder(task.text).build()
                    val result = client.runInference(request).await()
                    val output = result.results.firstOrNull()?.text
                        ?: "No rewriting suggestions were returned."
                    AiExecutionResult.Success(
                        taskLabel = "Rewriting (${task.tone.name.lowercase()})",
                        output = output
                    )
                }
            )
        } finally {
            client.close()
        }
    }

    private suspend fun executeWithFeatureReadiness(
        taskName: String,
        client: Any,
        startInference: suspend () -> AiExecutionResult
    ): AiExecutionResult {
        return try {
            when (val initialStatus = checkFeatureStatus(taskName, client)) {
                FeatureStatus.AVAILABLE -> {
                    Log.d(TAG, "Feature ready for $taskName. Starting inference.")
                }

                FeatureStatus.DOWNLOADABLE -> {
                    Log.d(TAG, "Feature downloadable for $taskName. Starting download.")
                    val downloadResult = downloadFeature(taskName, client)
                    if (downloadResult != null) return downloadResult

                    val postDownloadStatus = checkFeatureStatus(taskName, client)
                    if (postDownloadStatus != FeatureStatus.AVAILABLE) {
                        return featureStatusError(taskName, postDownloadStatus)
                    }
                }

                FeatureStatus.DOWNLOADING -> {
                    Log.d(
                        TAG,
                        "Feature already downloading for $taskName. Returning retryable status."
                    )
                    return featureStatusError(taskName, initialStatus)
                }

                else -> return featureStatusError(taskName, initialStatus)
            }

            Log.d(TAG, "Running inference for $taskName")
            val inferenceStartTime = System.currentTimeMillis()
            val result = startInference()
            val inferenceDuration = System.currentTimeMillis() - inferenceStartTime
            Log.d(TAG, "Inference for $taskName finished in ${inferenceDuration}ms")
            result
        } catch (error: Throwable) {
            Log.e(TAG, "Task execution failed for $taskName", error)
            val errorCode = (error as? GenAiException)?.errorCode
            val reason = when (errorCode) {
                606 -> "The specific AI capability for $taskName was not found on this device (Error 606). This usually means the required on-device model feature is not available or AICore needs an update."
                else -> "$taskName failed during execution."
            }
            AiExecutionResult.Error(
                reason = reason,
                diagnosticDetails = buildDiagnosticDetails(
                    taskName = taskName,
                    status = null,
                    detail = "${error::class.java.simpleName}: ${error.message ?: "No message"}"
                )
            )
        }
    }

    private suspend fun checkFeatureStatus(taskName: String, client: Any): Int {
        val status = when (client) {
            is Summarizer -> client.checkFeatureStatus().await()
            is Proofreader -> client.checkFeatureStatus().await()
            is Rewriter -> client.checkFeatureStatus().await()
            else -> error("Unsupported client type: ${client::class.qualifiedName}")
        }
        Log.d(TAG, "Feature status for $taskName=${statusToName(status)} ($status)")
        return status
    }

    private suspend fun downloadFeature(taskName: String, client: Any): AiExecutionResult? {
        return try {
            suspendCancellableCoroutine { continuation ->
                val callback = object : DownloadCallback {
                    override fun onDownloadStarted(bytesToDownload: Long) {
                        Log.d(TAG, "Download started for $taskName. bytes=$bytesToDownload")
                    }

                    override fun onDownloadProgress(totalBytesDownloaded: Long) {
                        Log.d(
                            TAG,
                            "Download progress for $taskName. downloaded=$totalBytesDownloaded"
                        )
                    }

                    override fun onDownloadCompleted() {
                        Log.d(TAG, "Download completed for $taskName")
                        if (continuation.isActive) continuation.resume(null)
                    }

                    override fun onDownloadFailed(e: GenAiException) {
                        Log.e(TAG, "Download failed for $taskName", e)
                        if (continuation.isActive) {
                            continuation.resume(
                                AiExecutionResult.Error(
                                    reason = "$taskName model download failed.",
                                    diagnosticDetails = buildDiagnosticDetails(
                                        taskName = taskName,
                                        status = FeatureStatus.DOWNLOADABLE,
                                        detail = "${e::class.java.simpleName}: ${e.message ?: "No message"}"
                                    )
                                )
                            )
                        }
                    }
                }

                when (client) {
                    is Summarizer -> client.downloadFeature(callback)
                    is Proofreader -> client.downloadFeature(callback)
                    is Rewriter -> client.downloadFeature(callback)
                    else -> continuation.resumeWithException(
                        IllegalArgumentException("Unsupported client type: ${client::class.qualifiedName}")
                    )
                }
            }
        } catch (error: Throwable) {
            Log.e(TAG, "Download orchestration failed for $taskName", error)
            AiExecutionResult.Error(
                reason = "$taskName model download failed.",
                diagnosticDetails = buildDiagnosticDetails(
                    taskName = taskName,
                    status = FeatureStatus.DOWNLOADABLE,
                    detail = "${error::class.java.simpleName}: ${error.message ?: "No message"}"
                )
            )
        }
    }

    private fun featureStatusError(taskName: String, status: Int): AiExecutionResult.Error {
        val reason = when (status) {
            FeatureStatus.UNAVAILABLE -> {
                "$taskName is unavailable on this device. ML Kit GenAI feature APIs require a supported device, Android AICore readiness, and a locked bootloader."
            }

            FeatureStatus.DOWNLOADABLE -> {
                "$taskName is not ready yet because its on-device model still needs to be downloaded."
            }

            FeatureStatus.DOWNLOADING -> {
                "$taskName is still preparing its on-device model. Retry after the download completes."
            }

            else -> "$taskName failed because the feature state was ${statusToName(status)}."
        }
        return AiExecutionResult.Error(
            reason = reason,
            diagnosticDetails = buildDiagnosticDetails(
                taskName = taskName,
                status = status,
                detail = "Check logcat tag $TAG for full execution trace. Ensure AICore is initialized and the device is in ML Kit GenAI's supported list."
            )
        )
    }

    private fun statusToName(status: Int): String {
        return when (status) {
            FeatureStatus.UNAVAILABLE -> "UNAVAILABLE"
            FeatureStatus.DOWNLOADABLE -> "DOWNLOADABLE"
            FeatureStatus.DOWNLOADING -> "DOWNLOADING"
            FeatureStatus.AVAILABLE -> "AVAILABLE"
            else -> "UNKNOWN"
        }
    }

    private fun buildDiagnosticDetails(
        taskName: String,
        status: Int?,
        detail: String
    ): String {
        val statusText = status?.let { "${statusToName(it)} ($it)" } ?: "not available"
        return "task=$taskName, featureStatus=$statusText, detail=$detail"
    }

    private fun AiTask.describeForLog(): String {
        return when (this) {
            is AiTask.Summarize -> "Summarize(length=${text.length})"
            is AiTask.Proofread -> "Proofread(length=${text.length})"
            is AiTask.Rewrite -> "Rewrite(tone=${tone.name}, length=${text.length})"
        }
    }

    private fun AiTask.humanLabel(): String {
        return when (this) {
            is AiTask.Summarize -> "Summarization"
            is AiTask.Proofread -> "Proofreading"
            is AiTask.Rewrite -> "Rewriting (${tone.name.lowercase()})"
        }
    }
}
