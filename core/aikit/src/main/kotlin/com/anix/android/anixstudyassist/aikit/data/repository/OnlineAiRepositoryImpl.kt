package com.anix.android.anixstudyassist.aikit.data.repository

import com.anix.android.anixstudyassist.aikit.BuildConfig
import com.anix.android.anixstudyassist.aikit.domain.repository.OnlineAiRepository
import com.google.ai.client.generativeai.GenerativeModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnlineAiRepositoryImpl @Inject constructor() : OnlineAiRepository {

    private val apiKey = BuildConfig.GEMINI_API_KEY

    private val chatModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )

    override suspend fun getConversationalResponse(prompt: String): String {
        return try {
            val response = chatModel.generateContent(prompt)
            response.text ?: "I'm sorry, I couldn't generate a response."
        } catch (e: Exception) {
            "Error: ${e.localizedMessage}"
        }
    }

    override suspend fun getSearchResponse(query: String): String {
        // Since Search Tool is not available in this SDK version (0.9.0),
        // we use a system prompt to encourage the model to provide up-to-date info
        // or acknowledge it's using its internal knowledge.
        val searchPrompt =
            "Search for information on: $query. Please provide the most accurate and up-to-date information possible based on your training data."
        return getConversationalResponse(searchPrompt)
    }
}
