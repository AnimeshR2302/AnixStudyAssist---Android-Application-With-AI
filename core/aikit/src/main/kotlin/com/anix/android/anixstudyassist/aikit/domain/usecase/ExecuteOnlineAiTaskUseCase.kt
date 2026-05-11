package com.anix.android.anixstudyassist.aikit.domain.usecase

import android.util.Log
import com.anix.android.anixstudyassist.aikit.domain.repository.OnlineAiRepository
import java.util.Locale
import javax.inject.Inject

class ExecuteOnlineAiTaskUseCase @Inject constructor(
    private val onlineAiRepository: OnlineAiRepository
) {
    companion object {
        private const val TAG = "ANIX_ExecOnline"
    }

    suspend operator fun invoke(message: String): String {
        Log.d(TAG, "invoke: message='$message'")
        val lowercase = message.lowercase(Locale.ROOT)
        val result = if (lowercase.startsWith("search") || lowercase.startsWith("look up")) {
            val query = message.substringAfter("search").substringAfter("look up").trim()
            Log.d(TAG, "detected search query: '$query'")
            onlineAiRepository.getSearchResponse(query)
        } else {
            Log.d(TAG, "falling back to conversational response")
            onlineAiRepository.getConversationalResponse(message)
        }
        Log.d(TAG, "invoke result length: ${result.length}")
        return result
    }
}
