package com.anix.android.anixstudyassist.feature.ai.domain.repository

import com.anix.android.anixstudyassist.feature.ai.domain.model.AiExecutionResult
import com.anix.android.anixstudyassist.feature.ai.domain.model.AiTask

interface OnDeviceAiRepository {
    suspend fun execute(task: AiTask): AiExecutionResult
}
