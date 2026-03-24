package com.anix.android.anixstudyassist.aikit.domain.repository

import com.anix.android.anixstudyassist.aikit.domain.model.AiExecutionResult
import com.anix.android.anixstudyassist.aikit.domain.model.AiTask

interface OnDeviceAiRepository {
    suspend fun execute(task: AiTask): AiExecutionResult
}
