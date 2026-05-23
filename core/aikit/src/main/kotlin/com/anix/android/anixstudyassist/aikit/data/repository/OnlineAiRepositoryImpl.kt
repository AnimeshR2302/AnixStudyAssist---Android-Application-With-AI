package com.anix.android.anixstudyassist.aikit.data.repository

import android.util.Log
import com.anix.android.anixstudyassist.aikit.BuildConfig
import com.anix.android.anixstudyassist.aikit.domain.model.OnlineAiConfig
import com.anix.android.anixstudyassist.aikit.domain.model.OnlineAiResult
import com.anix.android.anixstudyassist.aikit.domain.repository.OnlineAiRepository
import com.google.genai.Client
import com.google.genai.types.GenerateContentConfig
import com.google.genai.types.GoogleSearch
import com.google.genai.types.Tool
import kotlinx.coroutines.future.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnlineAiRepositoryImpl @Inject constructor() : OnlineAiRepository {

    companion object {
        private const val TAG = "ANIX_OnlineAi"
    }

    private val apiKey = BuildConfig.GEMINI_API_KEY
    private val modelId = OnlineAiConfig.MODEL_ID
    private val client: Client by lazy { Client.builder().apiKey(apiKey).build() }

    override suspend fun getConversationalResponse(prompt: String): OnlineAiResult {
        Log.d(TAG, "getConversationalResponse started. Prompt length: ${prompt.length}")
        return runGeneration(
            prompt = prompt,
            config = null,
            usedSearchGrounding = false,
            operationName = "getConversationalResponse"
        )
    }

    override suspend fun getSearchResponse(query: String): OnlineAiResult {
        Log.d(TAG, "getSearchResponse started. Query length: ${query.length}")
        val config = GenerateContentConfig.builder()
            .tools(listOf(Tool.builder().googleSearch(GoogleSearch.builder().build()).build()))
            .build()

        val groundedResult = runGeneration(
            prompt = query,
            config = config,
            usedSearchGrounding = true,
            operationName = "getSearchResponse"
        )

        if (groundedResult is OnlineAiResult.Success) return groundedResult

        Log.d(TAG, "Falling back to basic generation for search query")
        return getConversationalResponse(query)
    }

    private suspend fun runGeneration(
        prompt: String,
        config: GenerateContentConfig?,
        usedSearchGrounding: Boolean,
        operationName: String
    ): OnlineAiResult {
        if (apiKey.isBlank()) {
            return OnlineAiResult.Error(
                reason = "Gemini API key is missing. Add GEMINI_API_KEY to local.properties."
            )
        }

        val startTime = System.currentTimeMillis()
        return try {
            val response = client.async.models.generateContent(modelId, prompt, config).await()
            val duration = System.currentTimeMillis() - startTime
            Log.d(TAG, "$operationName successful. Duration: ${duration}ms model=$modelId")

            val text = response.text()
            if (text.isNullOrBlank()) {
                OnlineAiResult.Error(
                    reason = "Gemini returned an empty response.",
                    diagnosticDetails = "model=$modelId operation=$operationName"
                )
            } else {
                OnlineAiResult.Success(
                    output = text,
                    usedSearchGrounding = usedSearchGrounding
                )
            }
        } catch (error: Exception) {
            val duration = System.currentTimeMillis() - startTime
            Log.e(TAG, "$operationName failed after ${duration}ms. Error: ${error.message}", error)
            OnlineAiResult.Error(
                reason = mapOnlineError(error),
                diagnosticDetails = "model=$modelId operation=$operationName detail=${error::class.java.simpleName}: ${error.message ?: "No message"}"
            )
        }
    }

    private fun mapOnlineError(error: Exception): String {
        val message = error.localizedMessage ?: error.message ?: "Unknown error"
        return when {
            message.contains("404") || message.contains("not found", ignoreCase = true) ->
                "AI model not found. Check the Gemini model configuration."

            message.contains("401") ||
                    message.contains("403") ||
                    message.contains("API key", ignoreCase = true) ||
                    message.contains("API_KEY_INVALID") ->
                "Invalid API key. Please check your Gemini API key in settings."

            message.contains("quota", ignoreCase = true) || message.contains("429") ->
                "API quota exceeded. Please try again later."

            message.contains("Unable to resolve host", ignoreCase = true) ||
                    message.contains("timeout", ignoreCase = true) ||
                    message.contains("couldn't connect", ignoreCase = true) ||
                    message.contains("failed to connect", ignoreCase = true) ->
                "Could not connect to Gemini. Check your internet connection and try again."

            message.contains("agent", ignoreCase = true) ->
                "Gemini rejected the model or agent configuration. Check the configured model ID."

            else -> message.substringBefore("\n").take(150)
        }
    }
}
