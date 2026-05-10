package com.anix.android.anixstudyassist.aikit.domain.usecase

import android.util.Log
import com.anix.android.anixstudyassist.aikit.domain.model.AiTask
import com.anix.android.anixstudyassist.aikit.domain.model.RewriteTone
import javax.inject.Inject

class ParseAiTaskUseCase @Inject constructor() {

    companion object {
        private const val TAG = "ANIX_AiParse"
    }

    operator fun invoke(rawMessage: String): AiTask? {
        val message = rawMessage.trim()
        val lowercase = message.lowercase()

        if (lowercase.startsWith("summarize:")) {
            val content = message.substringAfter(":", "").trim()
            if (content.isNotEmpty()) {
                return AiTask.Summarize(content).also {
                    Log.d(TAG, "Parsed summarize command. textLength=${content.length}")
                }
            }
        }

        if (lowercase.startsWith("proofread:")) {
            val content = message.substringAfter(":", "").trim()
            if (content.isNotEmpty()) {
                return AiTask.Proofread(content).also {
                    Log.d(TAG, "Parsed proofread command. textLength=${content.length}")
                }
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
                return AiTask.Rewrite(content, tone).also {
                    Log.d(
                        TAG,
                        "Parsed rewrite command. tone=${tone.name.lowercase()} textLength=${content.length}"
                    )
                }
            }
        }

        Log.d(TAG, "No AI task matched for input='$message'")
        return null
    }
}
