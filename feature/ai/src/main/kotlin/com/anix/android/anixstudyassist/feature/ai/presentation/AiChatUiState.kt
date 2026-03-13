package com.anix.android.anixstudyassist.feature.ai.presentation

data class ChatMessage(
    val text: String,
    val time: String,
    val isFromUser: Boolean
)

data class AiChatUiState(
    val inputText: String = "",
    val isBusy: Boolean = false,
    val messages: List<ChatMessage> = listOf(
        ChatMessage(
            text = "Hello! I'm AnixAI. Send 'hi' to see what I can do on-device.",
            time = "Now",
            isFromUser = false
        )
    )
)
