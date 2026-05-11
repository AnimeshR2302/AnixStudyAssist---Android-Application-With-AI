package com.anix.android.anixstudyassist.aichat.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anix.android.anixstudyassist.aichat.presentation.state.AiChatUiState
import com.anix.android.anixstudyassist.aichat.presentation.state.ChatMessage
import com.anix.android.anixstudyassist.aichat.presentation.voice.VoiceManager
import com.anix.android.anixstudyassist.aikit.domain.model.AiExecutionResult
import com.anix.android.anixstudyassist.aikit.domain.usecase.ExecuteOnDeviceAiTaskUseCase
import com.anix.android.anixstudyassist.aikit.domain.usecase.ExecuteOnlineAiTaskUseCase
import com.anix.android.anixstudyassist.aikit.domain.usecase.GetCapabilitiesUseCase
import com.anix.android.anixstudyassist.aikit.domain.usecase.ParseAiTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
    private val executeOnDeviceAiTaskUseCase: ExecuteOnDeviceAiTaskUseCase,
    private val executeOnlineAiTaskUseCase: ExecuteOnlineAiTaskUseCase,
    private val voiceManager: VoiceManager
) : ViewModel() {

    companion object {
        private const val TAG = "ANIX_AiChat"
    }

    private val formatter = DateTimeFormatter.ofPattern("hh:mm a")

    private var errorClearJob: Job? = null

    private val _uiState = MutableStateFlow(AiChatUiState())
    val uiState: StateFlow<AiChatUiState> = _uiState.asStateFlow()

    fun onInputChanged(value: String) {
        _uiState.update { it.copy(inputText = value) }
    }

    private fun setErrorMessage(message: String?) {
        errorClearJob?.cancel()
        _uiState.update { it.copy(errorMessage = message) }
        if (message != null) {
            errorClearJob = viewModelScope.launch {
                delay(5000)
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }

    fun onSendClicked() {
        val text = _uiState.value.inputText.trim()
        Log.d(TAG, "onSendClicked: input='$text'")
        if (text.isBlank() || _uiState.value.isBusy) return

        processInput(text, isVoice = false)
    }

    fun onMicClicked() {
        setErrorMessage(null)
        val isVoiceViewActive = _uiState.value.isVoiceViewActive
        val isListening = _uiState.value.isListening
        Log.d(TAG, "onMicClicked: isVoiceViewActive=$isVoiceViewActive, isListening=$isListening")

        if (!isVoiceViewActive) {
            Log.d(TAG, "onMicClicked: Switching to Voice View and starting listener")
            _uiState.update {
                it.copy(
                    isVoiceViewActive = true,
                    isListening = true,
                    voiceTranscription = ""
                )
            }
            startVoiceListening()
        } else {
            if (isListening) {
                Log.d(TAG, "onMicClicked: Stopping listener")
                voiceManager.stopListening()
                _uiState.update { it.copy(isListening = false) }
            } else {
                Log.d(TAG, "onMicClicked: Restarting listener")
                _uiState.update { it.copy(isListening = true, voiceTranscription = "") }
                startVoiceListening()
            }
        }
    }

    fun onBackToTextView() {
        setErrorMessage(null)
        _uiState.update { it.copy(isVoiceViewActive = false, isListening = false) }
        voiceManager.stopListening()
    }

    fun onSendVoiceClicked() {
        val text = _uiState.value.voiceTranscription
        Log.d(TAG, "onSendVoiceClicked: transcription='$text'")
        if (text.isBlank() || _uiState.value.isBusy) return

        processInput(text, isVoice = true)
        _uiState.update { it.copy(voiceTranscription = "") }
        setErrorMessage(null)
    }

    private fun startVoiceListening() {
        voiceManager.startListening(
            onResult = { result ->
                if (_uiState.value.isVoiceViewActive) {
                    _uiState.update { it.copy(isListening = false, voiceTranscription = result) }
                    setErrorMessage(null)
                }
            },
            onError = { error ->
                if (_uiState.value.isVoiceViewActive) {
                    Log.e(TAG, "Voice Error: $error")
                    _uiState.update { it.copy(isListening = false) }
                    setErrorMessage(error)
                } else {
                    _uiState.update { it.copy(isListening = false) }
                }
            }
        )
    }

    private fun processInput(text: String, isVoice: Boolean) {
        Log.d(TAG, "processInput: text='$text', isVoice=$isVoice")
        _uiState.update { it.copy(inputText = "", isBusy = true) }
        setErrorMessage(null)
        appendMessage(text = text, isFromUser = true, isVoice = isVoice)

        viewModelScope.launch {
            val lowercaseText = text.lowercase(java.util.Locale.ROOT)
            val reply = when {
                lowercaseText == "hi" -> {
                    Log.d(TAG, "processInput: Handled as 'hi' command")
                    buildCapabilitiesReply()
                }
                else -> {
                    val task = parseAiTaskUseCase(text)
                    if (task == null) {
                        Log.d(
                            TAG,
                            "processInput: No on-device task, calling ExecuteOnlineAiTaskUseCase"
                        )
                        executeOnlineAiTaskUseCase(text)
                    } else {
                        Log.d(
                            TAG,
                            "processInput: Task parsed=$task, calling ExecuteOnDeviceAiTaskUseCase"
                        )
                        when (val result = executeOnDeviceAiTaskUseCase(task)) {
                            is AiExecutionResult.Success -> {
                                Log.d(
                                    TAG,
                                    "processInput: On-device task succeeded. outputLength=${result.output.length}"
                                )
                                "Success (${result.taskLabel}):\n${result.output}"
                            }

                            is AiExecutionResult.Error -> {
                                Log.e(
                                    TAG,
                                    "processInput: On-device task failed. reason=${result.reason}"
                                )
                                "Error: ${result.reason}"
                            }
                        }
                    }
                }
            }

            Log.d(TAG, "processInput: Final reply generated. Length=${reply.length}")
            if (reply.startsWith("Error:")) {
                _uiState.update { it.copy(isBusy = false) }
                // Only show error if we haven't switched away from the mode that initiated the request
                if (!isVoice || _uiState.value.isVoiceViewActive) {
                    setErrorMessage(reply)
                }
            } else {
                appendMessage(text = reply, isFromUser = false, isVoice = isVoice)
                if (isVoice) {
                    voiceManager.speak(reply)
                }
                _uiState.update { it.copy(isBusy = false) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.shutdown()
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

    private fun appendMessage(text: String, isFromUser: Boolean, isVoice: Boolean = false) {
        val message = ChatMessage(
            text = text,
            time = LocalTime.now().format(formatter),
            isFromUser = isFromUser,
            isVoice = isVoice
        )
        _uiState.update { it.copy(messages = it.messages + message) }
    }
}