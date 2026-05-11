package com.anix.android.anixstudyassist.aichat.presentation.state

data class ChatMessage(
    val text: String,
    val time: String,
    val isFromUser: Boolean,
    val isVoice: Boolean = false
)

data class AiChatUiState(
    val inputText: String = "",
    val voiceTranscription: String = "",
    val isBusy: Boolean = false,
    val isListening: Boolean = false,
    val isVoiceViewActive: Boolean = false,
    val errorMessage: String? = null,
    val messages: List<ChatMessage> = listOf(
        ChatMessage(
            text = "Hello! I'm AnixAI. Send 'hi' to see what I can do on-device.",
            time = "Now",
            isFromUser = false
        )
    )
)
