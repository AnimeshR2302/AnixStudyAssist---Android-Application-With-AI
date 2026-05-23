package com.anix.android.anixstudyassist.aikit.domain.usecase

import android.util.Log
import com.anix.android.anixstudyassist.aikit.domain.model.OnlineAiResult
import com.anix.android.anixstudyassist.aikit.domain.repository.OnlineAiRepository
import java.util.Locale
import javax.inject.Inject

class ExecuteOnlineAiTaskUseCase @Inject constructor(
    private val onlineAiRepository: OnlineAiRepository
) {
    companion object {
        private const val TAG = "ANIX_ExecOnline"
    }

    suspend operator fun invoke(message: String): OnlineAiResult {
        Log.d(TAG, "invoke: message='$message'")
        val lowercase = message.lowercase(Locale.ROOT)
        val result = if (lowercase.startsWith("search") || lowercase.startsWith("look up")) {
            val query = extractSearchQuery(message)
            Log.d(TAG, "detected search query: '$query'")
            if (query.isBlank()) {
                OnlineAiResult.Error("Please include something to search for.")
            } else {
                onlineAiRepository.getSearchResponse(query)
            }
        } else {
            Log.d(TAG, "falling back to conversational response")
            onlineAiRepository.getConversationalResponse(message)
        }
        Log.d(
            TAG,
            "invoke result: ${if (result is OnlineAiResult.Success) "Success" else "Error"}"
        )
        return result
    }

    private fun extractSearchQuery(message: String): String {
        val trimmed = message.trim()
        return when {
            trimmed.startsWith("search", ignoreCase = true) ->
                trimmed.substringAfter("search", "").trimStart(':', ' ')

            trimmed.startsWith("look up", ignoreCase = true) ->
                trimmed.substringAfter("look up", "").trimStart(':', ' ')

            else -> trimmed
        }
    }
}
