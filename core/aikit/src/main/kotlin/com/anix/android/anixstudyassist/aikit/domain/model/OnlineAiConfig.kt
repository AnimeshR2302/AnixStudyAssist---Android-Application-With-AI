package com.anix.android.anixstudyassist.aikit.domain.model

import com.anix.android.anixstudyassist.aikit.BuildConfig

object OnlineAiConfig {
    const val MODEL_ID = "gemini-2.5-flash"
    const val MODEL_DISPLAY_NAME = "Gemini 2.5 Flash"

    val isApiKeyConfigured: Boolean
        get() = BuildConfig.GEMINI_API_KEY.isNotBlank()
}
