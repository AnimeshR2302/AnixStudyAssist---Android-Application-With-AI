package com.anix.android.anixstudyassist.aikit.domain.usecase

import android.util.Log
import com.anix.android.anixstudyassist.aikit.domain.model.AiTask
import com.anix.android.anixstudyassist.aikit.domain.model.RewriteTone
import java.util.Locale
import javax.inject.Inject

class ParseAiTaskUseCase @Inject constructor() {

    companion object {
        private const val TAG = "ANIX_AiParse"
    }

    operator fun invoke(rawMessage: String): AiTask? {
        Log.d(TAG, "invoke: rawMessage='$rawMessage'")
        val message = rawMessage.trim()
        if (message.isEmpty()) {
            Log.d(TAG, "invoke: empty message")
            return null
        }

        val lowercase = message.lowercase(Locale.ROOT)

        val task = when {
            lowercase.startsWith("summarize") -> {
                val content = extractContent(message, 9)
                if (content.isNotEmpty()) {
                    AiTask.Summarize(content).also {
                        Log.d(TAG, "Parsed summarize command. textLength=${content.length}")
                    }
                } else null
            }

            lowercase.startsWith("proofread") -> {
                val content = extractContent(message, 9)
                if (content.isNotEmpty()) {
                    AiTask.Proofread(content).also {
                        Log.d(TAG, "Parsed proofread command. textLength=${content.length}")
                    }
                } else null
            }

            lowercase.startsWith("rewrite") -> {
                val body = if (message.length > 7) message.substring(7).trim() else ""
                if (body.isEmpty()) {
                    Log.d(TAG, "Rewrite command: body is empty")
                    null
                } else {
                    // Identify tone and content by finding the first colon or space
                    val firstSpace = body.indexOf(' ')
                    val firstColon = body.indexOf(':')

                    val delimiterIndex = when {
                        firstColon != -1 && (firstSpace == -1 || firstColon < firstSpace) -> firstColon
                        firstSpace != -1 -> firstSpace
                        else -> -1
                    }

                    if (delimiterIndex == -1) {
                        Log.d(TAG, "Rewrite command: no delimiter found in body '$body'")
                        null
                    } else {
                        val toneText =
                            body.substring(0, delimiterIndex).trim().lowercase(Locale.ROOT)
                        val content = body.substring(delimiterIndex + 1).trim()

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
                            AiTask.Rewrite(content, tone).also {
                                Log.d(
                                    TAG,
                                    "Parsed rewrite command. tone=${tone.name.lowercase(Locale.ROOT)} textLength=${content.length}"
                                )
                            }
                        } else {
                            Log.d(
                                TAG,
                                "Rewrite command failed validation. tone=$toneText contentEmpty=${content.isEmpty()}"
                            )
                            null
                        }
                    }
                }
            }

            else -> {
                Log.d(TAG, "No AI task matched for input='$message'")
                null
            }
        }

        Log.d(TAG, "invoke result: ${task?.javaClass?.simpleName ?: "null"}")
        return task
    }

    private fun extractContent(message: String, keywordLength: Int): String {
        if (message.length <= keywordLength) return ""
        val afterKeyword = message.substring(keywordLength).trim()
        return if (afterKeyword.startsWith(":")) {
            afterKeyword.substring(1).trim()
        } else {
            afterKeyword
        }
    }
}
