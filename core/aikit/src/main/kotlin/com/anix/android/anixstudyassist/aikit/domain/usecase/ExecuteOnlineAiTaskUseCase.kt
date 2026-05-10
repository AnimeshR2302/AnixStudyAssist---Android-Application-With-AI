package com.anix.android.anixstudyassist.aikit.domain.usecase

import com.anix.android.anixstudyassist.aikit.domain.repository.OnlineAiRepository
import java.util.Locale
import javax.inject.Inject

class ExecuteOnlineAiTaskUseCase @Inject constructor(
    private val onlineAiRepository: OnlineAiRepository
) {
    suspend operator fun invoke(message: String): String {
        val lowercase = message.lowercase(Locale.ROOT)
        return if (lowercase.startsWith("search") || lowercase.startsWith("look up")) {
            val query = message.substringAfter("search").substringAfter("look up").trim()
            onlineAiRepository.getSearchResponse(query)
        } else {
            onlineAiRepository.getConversationalResponse(message)
        }
    }
}
