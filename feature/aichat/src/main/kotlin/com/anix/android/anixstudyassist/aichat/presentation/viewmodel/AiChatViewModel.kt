package com.anix.android.anixstudyassist.aichat.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anix.android.anixstudyassist.aichat.presentation.state.AiChatUiState
import com.anix.android.anixstudyassist.aichat.presentation.state.ChatMessage
import com.anix.android.anixstudyassist.aikit.domain.model.AiExecutionResult
import com.anix.android.anixstudyassist.aikit.domain.usecase.ExecuteOnDeviceAiTaskUseCase
import com.anix.android.anixstudyassist.aikit.domain.usecase.GetCapabilitiesUseCase
import com.anix.android.anixstudyassist.aikit.domain.usecase.ParseAiTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class AiChatViewModel @Inject constructor(
    private val getCapabilitiesUseCase: GetCapabilitiesUseCase,
    private val parseAiTaskUseCase: ParseAiTaskUseCase,
    private val executeOnDeviceAiTaskUseCase: ExecuteOnDeviceAiTaskUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "ANIX_AiChat"
    }

    private val formatter = DateTimeFormatter.ofPattern("hh:mm a")

    private val _uiState = MutableStateFlow(AiChatUiState())
    val uiState: StateFlow<AiChatUiState> = _uiState.asStateFlow()

    fun onInputChanged(value: String) {
        _uiState.update { it.copy(inputText = value) }
    }

    fun onSendClicked() {
        val text = _uiState.value.inputText.trim()
        if (text.isBlank() || _uiState.value.isBusy) return

        Log.d(TAG, "Received chat input='$text'")
        appendMessage(text = text, isFromUser = true)
        _uiState.update { it.copy(inputText = "", isBusy = true) }

        viewModelScope.launch {
            val reply = when {
                text.equals("hi", ignoreCase = true) -> buildCapabilitiesReply()
                else -> {
                    val task = parseAiTaskUseCase(text)
                    if (task == null) {
                        Log.d(TAG, "No executable AI task produced for input='$text'")
                        "I can't run that on-device yet. Send 'hi' to view supported commands."
                    } else {
                        Log.d(TAG, "Dispatching parsed task=$task")
                        when (val result = executeOnDeviceAiTaskUseCase(task)) {
                            is AiExecutionResult.Success -> {
                                Log.d(
                                    TAG,
                                    "Task succeeded. label=${result.taskLabel} outputLength=${result.output.length}"
                                )
                                "Success (${result.taskLabel}):\n${result.output}"
                            }

                            is AiExecutionResult.Error -> {
                                Log.e(
                                    TAG,
                                    "Task failed. reason=${result.reason} details=${result.diagnosticDetails}"
                                )
                                buildString {
                                    append("Error: ${result.reason}")
                                    result.diagnosticDetails?.takeIf { it.isNotBlank() }?.let {
                                        append("\n\nDiagnostic details:\n$it")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            appendMessage(text = reply, isFromUser = false)
            _uiState.update { it.copy(isBusy = false) }
        }
    }

    private fun buildCapabilitiesReply(): String {
        val capabilities = getCapabilitiesUseCase()
        val lines = capabilities.mapIndexed { index, capability ->
            "${index + 1}. ${capability.command} - ${capability.description}"
        }

        return buildString {
            appendLine("Here are my on-device capabilities:")
            lines.forEach { appendLine(it) }
        }.trim()
    }

    private fun appendMessage(text: String, isFromUser: Boolean) {
        val message = ChatMessage(
            text = text,
            time = LocalTime.now().format(formatter),
            isFromUser = isFromUser
        )
        _uiState.update { it.copy(messages = it.messages + message) }
    }
}