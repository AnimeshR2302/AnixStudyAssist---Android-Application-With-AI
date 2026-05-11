package com.anix.android.anixstudyassist.aikit.domain.usecase

import android.util.Log
import com.anix.android.anixstudyassist.aikit.domain.model.AiExecutionResult
import com.anix.android.anixstudyassist.aikit.domain.model.AiTask
import com.anix.android.anixstudyassist.aikit.domain.repository.OnDeviceAiRepository
import javax.inject.Inject

class ExecuteOnDeviceAiTaskUseCase @Inject constructor(
    private val repository: OnDeviceAiRepository
) {
    companion object {
        private const val TAG = "ANIX_ExecOnDevice"
    }

    suspend operator fun invoke(task: AiTask): AiExecutionResult {
        Log.d(TAG, "invoke: task=$task")
        val result = repository.executeOnDeviceTask(task)
        Log.d(
            TAG,
            "invoke result: ${if (result is AiExecutionResult.Success) "Success" else "Error"}"
        )
        return result
    }
}
