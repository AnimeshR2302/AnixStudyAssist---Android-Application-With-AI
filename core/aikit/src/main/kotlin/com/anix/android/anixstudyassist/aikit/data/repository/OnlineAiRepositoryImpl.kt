package com.anix.android.anixstudyassist.aikit.data.repository

import android.util.Log
import com.anix.android.anixstudyassist.aikit.BuildConfig
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

    private val apiKey = BuildConfig.GEMINI_API_KEY
    private val client = Client.builder().apiKey(apiKey).build()
    private val modelId = "gemini-2.0-flash"

    override suspend fun getConversationalResponse(prompt: String): String {
        return try {
            val response = client.async.models.generateContent(modelId, prompt, null).await()
            response.text() ?: "I'm sorry, I couldn't generate a response."
        } catch (e: Exception) {
            Log.e("OnlineAiRepo", "Gemini API Error: ${e.message}", e)
            val message = e.localizedMessage ?: "Unknown error"
            val cleanMessage = when {
                message.contains("404") || message.contains("not found") ->
                    "AI model not found. Check your configuration or API key."

                message.contains("401") || message.contains("403") || message.contains("API key") || message.contains(
                    "API_KEY_INVALID"
                ) ->
                    "Invalid API key. Please check your Gemini API key in settings."

                message.contains("quota") || message.contains("429") ->
                    "API quota exceeded. Please try again later."

                else -> message.substringBefore("\n").take(150)
            }
            "Error: $cleanMessage"
        }
    }

    override suspend fun getSearchResponse(query: String): String {
        return try {
            val config = GenerateContentConfig.builder()
                .tools(listOf(Tool.builder().googleSearch(GoogleSearch.builder().build()).build()))
                .build()

            val response = client.async.models.generateContent(modelId, query, config).await()
            response.text() ?: "I'm sorry, I couldn't generate a response."
        } catch (e: Exception) {
            Log.e("OnlineAiRepo", "Gemini Search Error: ${e.message}", e)
            getConversationalResponse(query) // Fallback to basic generation
        }
    }
}
