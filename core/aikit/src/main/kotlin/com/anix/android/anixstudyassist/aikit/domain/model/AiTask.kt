package com.anix.android.anixstudyassist.aikit.domain.model

enum class RewriteTone {
    FRIENDLY,
    PROFESSIONAL,
    SHORTEN,
    REPHRASE,
    ELABORATE,
    EMOJIFY
}

sealed interface AiTask {
    data class Summarize(val text: String) : AiTask
    data class Proofread(val text: String) : AiTask
    data class Rewrite(val text: String, val tone: RewriteTone) : AiTask
}
