package com.anix.android.anixstudyassist.aichat.presentation.state

import com.anix.android.anixstudyassist.aikit.domain.model.RewriteTone

data class ChatMessage(
    val text: String,
    val time: String,
    val isFromUser: Boolean,
    val isVoice: Boolean = false
)

enum class TextChatCapability {
    SUMMARIZE,
    PROOFREAD,
    REWRITE
}

data class TextChatCapabilityOption(
    val capability: TextChatCapability,
    val title: String,
    val description: String
)

data class AiChatUiState(
    val inputText: String = "",
    val voiceTranscription: String = "",
    val isBusy: Boolean = false,
    val isListening: Boolean = false,
    val isVoiceViewActive: Boolean = false,
    val showRetryButton: Boolean = false,
    val lastProcessedText: String = "",
    val errorMessage: String? = null,
    val availableCapabilities: List<TextChatCapabilityOption> = emptyList(),
    val rewriteToneOptions: List<RewriteTone> = emptyList(),
    val selectedCapability: TextChatCapability? = null,
    val selectedRewriteTone: RewriteTone? = null,
    val messages: List<ChatMessage> = listOf(
        ChatMessage(
            text = "Hello! I'm AnixAI. Use the + button to pick an on-device capability, or send a normal message.",
            time = "Now",
            isFromUser = false
        )
    )
)
