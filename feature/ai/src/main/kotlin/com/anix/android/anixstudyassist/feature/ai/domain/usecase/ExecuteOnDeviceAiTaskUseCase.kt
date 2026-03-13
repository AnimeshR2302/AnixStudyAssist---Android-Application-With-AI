package com.anix.android.anixstudyassist.feature.ai.domain.usecase

import com.anix.android.anixstudyassist.feature.ai.domain.model.AiExecutionResult
import com.anix.android.anixstudyassist.feature.ai.domain.model.AiTask
import com.anix.android.anixstudyassist.feature.ai.domain.repository.OnDeviceAiRepository
import javax.inject.Inject

class ExecuteOnDeviceAiTaskUseCase @Inject constructor(
    private val repository: OnDeviceAiRepository
) {
    suspend operator fun invoke(task: AiTask): AiExecutionResult {
        return repository.execute(task)
    }
}
