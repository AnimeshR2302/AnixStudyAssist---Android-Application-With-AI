package com.anix.android.anixstudyassist.aichat.data.repository

// This file is deprecated. Use aikit module implementations directly.
// For backwards compatibility, re-export from aikit
@Deprecated(
    message = \ "Use com.anix.android.anixstudyassist.aikit.data.repository.OnDeviceAiRepositoryImpl instead\",
replaceWith = ReplaceWith(\"OnDeviceAiRepositoryImpl\", \"com.anix.android.anixstudyassist.aikit.data.repository.OnDeviceAiRepositoryImpl\")
)
typealias OnDeviceAiRepositoryImpl = com.anix.android.anixstudyassist.aikit.data.repository.OnDeviceAiRepositoryImpl

/** @deprecated This entire class implementation has moved to aikit module */
@Deprecated(\ "Use aikit module implementations directly\")

companion object {
    private const val TAG = "AI_EXECUTION"
}

private val taskExecutors: Map<KClass<out AiTask>, suspend (AiTask) -> AiExecutionResult> = mapOf(
    AiTask.Summarize::class to { task -> runSummarization(task as AiTask.Summarize) },
    AiTask.Proofread::class to { task -> runProofreading(task as AiTask.Proofread) },
    AiTask.Rewrite::class to { task -> runRewriting(task as AiTask.Rewrite) }
)

override suspend fun execute(task: AiTask): AiExecutionResult {
    val executor = taskExecutors[task::class]
        ?: return AiExecutionResult.Error(
            reason = "Unsupported on-device AI task.",
            diagnosticDetails = "No executor registered for ${task::class.qualifiedName}."
        )

    Log.d(TAG, "Executing task=${task.describeForLog()}")
    return try {
        executor(task)
    } catch (error: Throwable) {
        Log.e(TAG, "Unhandled task failure for ${task.describeForLog()}", error)
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
    val options = SummarizerOptions.builder(context)
        .setLanguage(SummarizerOptions.Language.ENGLISH)
        .setInputType(SummarizerOptions.InputType.ARTICLE)
        .setOutputType(SummarizerOptions.OutputType.THREE_BULLETS)
        .setLongInputAutoTruncationEnabled(true)
        .build()

    val client = Summarization.getClient(options)
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
    val initialStatus = checkFeatureStatus(taskName, client)
    when (initialStatus) {
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
                "Feature already downloading for $taskName. Inference request will wait on model availability."
            )
        }

        else -> return featureStatusError(taskName, initialStatus)
    }

    return try {
        Log.d(TAG, "Running inference for $taskName")
        startInference()
    } catch (error: Throwable) {
        Log.e(TAG, "Inference failed for $taskName", error)
        AiExecutionResult.Error(
            reason = "$taskName failed during inference.",
            diagnosticDetails = buildDiagnosticDetails(
                taskName = taskName,
                status = initialStatus,
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
                    Log.d(TAG, "Download progress for $taskName. downloaded=$totalBytesDownloaded")
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
