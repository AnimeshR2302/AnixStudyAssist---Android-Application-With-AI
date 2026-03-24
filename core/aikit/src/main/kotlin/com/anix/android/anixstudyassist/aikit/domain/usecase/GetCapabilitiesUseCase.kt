package com.anix.android.anixstudyassist.aikit.domain.usecase

import com.anix.android.anixstudyassist.aikit.domain.model.AiCapability
import javax.inject.Inject

class GetCapabilitiesUseCase @Inject constructor() {
    operator fun invoke(): List<AiCapability> {
        return listOf(
            AiCapability(
                command = "summarize: <text>",
                description = "Summarizes long text into concise bullets."
            ),
            AiCapability(
                command = "proofread: <text>",
                description = "Fixes grammar and spelling mistakes."
            ),
            AiCapability(
                command = "rewrite <friendly|professional|shorten|rephrase|elaborate|emojify>: <text>",
                description = "Rewrites text in a requested tone or style."
            )
        )
    }
}
