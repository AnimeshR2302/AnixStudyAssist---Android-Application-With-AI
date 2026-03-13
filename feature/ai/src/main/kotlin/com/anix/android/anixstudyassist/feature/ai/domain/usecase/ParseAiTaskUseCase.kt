package com.anix.android.anixstudyassist.feature.ai.domain.usecase

import com.anix.android.anixstudyassist.feature.ai.domain.model.AiTask
import com.anix.android.anixstudyassist.feature.ai.domain.model.RewriteTone
import javax.inject.Inject

class ParseAiTaskUseCase @Inject constructor() {

    operator fun invoke(rawMessage: String): AiTask? {
        val message = rawMessage.trim()
        val lowercase = message.lowercase()

        if (lowercase.startsWith("summarize:")) {
            val content = message.substringAfter(":", "").trim()
            if (content.isNotEmpty()) {
                return AiTask.Summarize(content)
            }
        }

        if (lowercase.startsWith("proofread:")) {
            val content = message.substringAfter(":", "").trim()
            if (content.isNotEmpty()) {
                return AiTask.Proofread(content)
            }
        }

        if (lowercase.startsWith("rewrite ")) {
            val body = message.substringAfter(" ", "").trim()
            val toneText = body.substringBefore(":", "").trim().lowercase()
            val content = body.substringAfter(":", "").trim()
            val tone = when (toneText) {
                "friendly" -> RewriteTone.FRIENDLY
                "professional" -> RewriteTone.PROFESSIONAL
                "shorten" -> RewriteTone.SHORTEN
                "rephrase" -> RewriteTone.REPHRASE
                "elaborate" -> RewriteTone.ELABORATE
                "emojify" -> RewriteTone.EMOJIFY
                else -> null
            }

            if (tone != null && content.isNotEmpty()) {
                return AiTask.Rewrite(content, tone)
            }
        }

        return null
    }
}
