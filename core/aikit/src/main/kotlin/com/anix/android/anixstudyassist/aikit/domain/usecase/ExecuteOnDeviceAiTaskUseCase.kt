package com.anix.android.anixstudyassist.aikit.domain.usecase

import com.anix.android.anixstudyassist.aikit.domain.model.AiExecutionResult
import com.anix.android.anixstudyassist.aikit.domain.model.AiTask
import com.anix.android.anixstudyassist.aikit.domain.repository.OnDeviceAiRepository
import javax.inject.Inject

class ExecuteOnDeviceAiTaskUseCase @Inject constructor(
    private val repository: OnDeviceAiRepository
) {
    suspend operator fun invoke(task: AiTask): AiExecutionResult {
        return repository.executeOnDeviceTask(task)
    }
}
