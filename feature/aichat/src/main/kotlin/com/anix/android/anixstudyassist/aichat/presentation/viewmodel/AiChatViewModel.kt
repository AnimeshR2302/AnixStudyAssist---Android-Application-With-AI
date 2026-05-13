package com.anix.android.anixstudyassist.aichat.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anix.android.anixstudyassist.aichat.presentation.state.AiChatUiState
import com.anix.android.anixstudyassist.aichat.presentation.state.ChatMessage
import com.anix.android.anixstudyassist.aichat.presentation.state.PendingRewriteSelection
import com.anix.android.anixstudyassist.aichat.presentation.state.TextChatCapability
import com.anix.android.anixstudyassist.aichat.presentation.state.TextChatCapabilityOption
import com.anix.android.anixstudyassist.aichat.presentation.voice.VoiceManager
import com.anix.android.anixstudyassist.aikit.domain.model.AiCapability
import com.anix.android.anixstudyassist.aikit.domain.model.AiExecutionResult
import com.anix.android.anixstudyassist.aikit.domain.model.AiTask
import com.anix.android.anixstudyassist.aikit.domain.model.RewriteTone
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
import kotlinx.coroutines.withTimeoutOrNull
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
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

    /** TODO: Assumption - rewrite tone menu order is fixed to match numeric reply parsing. */
    private val rewriteToneOptions = listOf(
        RewriteTone.FRIENDLY,
        RewriteTone.PROFESSIONAL,
        RewriteTone.SHORTEN,
        RewriteTone.REPHRASE,
        RewriteTone.ELABORATE,
        RewriteTone.EMOJIFY
    )

    private var errorClearJob: Job? = null

    private val _uiState = MutableStateFlow(AiChatUiState())
    val uiState: StateFlow<AiChatUiState> = _uiState.asStateFlow()

    init {
        _uiState.update {
            it.copy(availableCapabilities = getCapabilitiesUseCase().mapNotNull(::mapCapabilityOption))
        }
    }

    fun onInputChanged(value: String) {
        _uiState.update { it.copy(inputText = value) }
    }

    fun onCapabilitySelected(capability: TextChatCapability) {
        if (_uiState.value.isBusy || _uiState.value.pendingRewriteSelection != null) return
        setErrorMessage(null)
        _uiState.update { it.copy(selectedCapability = capability) }
    }

    fun onCapabilityCleared() {
        if (_uiState.value.isBusy || _uiState.value.pendingRewriteSelection != null) return
        _uiState.update { it.copy(selectedCapability = null) }
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

        val pendingRewriteSelection = _uiState.value.pendingRewriteSelection
        if (pendingRewriteSelection != null) {
            handleRewriteToneSelection(text, pendingRewriteSelection)
        } else {
            handleTextChatInput(text)
        }
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
                    voiceTranscription = "",
                    showRetryButton = false
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
                _uiState.update {
                    it.copy(
                        isListening = true,
                        voiceTranscription = "",
                        showRetryButton = false
                    )
                }
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

        processVoiceInput(text, isVoice = true)
        _uiState.update { it.copy(voiceTranscription = "", showRetryButton = false) }
        setErrorMessage(null)
    }

    fun onRetryClicked() {
        val lastText = _uiState.value.lastProcessedText
        val isVoice = _uiState.value.isVoiceViewActive
        Log.d(TAG, "onRetryClicked: retrying text='$lastText', isVoice=$isVoice")
        if (lastText.isBlank()) return

        _uiState.update { it.copy(showRetryButton = false) }
        processVoiceInput(lastText, isVoice = isVoice, isRetry = true)
    }

    private fun startVoiceListening() {
        voiceManager.startListening(
            onResult = { result ->
                if (_uiState.value.isVoiceViewActive) {
                    _uiState.update { it.copy(isListening = false, voiceTranscription = result) }
                    setErrorMessage(null)
                    if (result.isNotBlank()) {
                        onSendVoiceClicked()
                    }
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

    private fun handleTextChatInput(text: String) {
        val selectedCapability = _uiState.value.selectedCapability
        Log.d(TAG, "handleTextChatInput: selectedCapability=$selectedCapability text='$text'")

        _uiState.update {
            it.copy(
                inputText = "",
                isBusy = true,
                lastProcessedText = text,
                showRetryButton = false,
                selectedCapability = null
            )
        }
        setErrorMessage(null)
        appendMessage(text = text, isFromUser = true)

        when (selectedCapability) {
            null -> {
                /** TODO: Future - replace placeholder normal chat response with actual conversational AI flow. */
                appendMessage(
                    text = "Normal chat response is not implemented yet.",
                    isFromUser = false
                )
                _uiState.update { it.copy(isBusy = false) }
            }

            TextChatCapability.SUMMARIZE -> executeTextTask(AiTask.Summarize(text))
            TextChatCapability.PROOFREAD -> executeTextTask(AiTask.Proofread(text))
            TextChatCapability.REWRITE -> {
                val pendingRewriteSelection = PendingRewriteSelection(
                    sourceText = text,
                    toneOptions = rewriteToneOptions
                )
                /** TODO: Future - refine assistant prompt copy once final chat UX is defined. */
                appendMessage(
                    text = buildRewriteTonePrompt(rewriteToneOptions),
                    isFromUser = false
                )
                _uiState.update {
                    it.copy(
                        isBusy = false,
                        pendingRewriteSelection = pendingRewriteSelection
                    )
                }
            }
        }
    }

    private fun handleRewriteToneSelection(
        selection: String,
        pendingRewriteSelection: PendingRewriteSelection
    ) {
        Log.d(TAG, "handleRewriteToneSelection: selection='$selection'")
        _uiState.update {
            it.copy(
                inputText = "",
                isBusy = true,
                showRetryButton = false
            )
        }
        setErrorMessage(null)
        appendMessage(text = selection, isFromUser = true)

        val selectedTone = parseRewriteToneSelection(selection, pendingRewriteSelection.toneOptions)
        if (selectedTone == null) {
            appendMessage(
                text = "Please reply with a valid rewrite option number or tone name.",
                isFromUser = false
            )
            _uiState.update { it.copy(isBusy = false) }
            return
        }

        _uiState.update { it.copy(pendingRewriteSelection = null) }
        executeTextTask(AiTask.Rewrite(pendingRewriteSelection.sourceText, selectedTone))
    }

    private fun executeTextTask(task: AiTask) {
        viewModelScope.launch {
            when (val result = executeOnDeviceAiTaskUseCase(task)) {
                is AiExecutionResult.Success -> {
                    appendMessage(
                        text = "Success (${result.taskLabel}):\n${result.output}",
                        isFromUser = false
                    )
                    _uiState.update { it.copy(isBusy = false) }
                }

                is AiExecutionResult.Error -> {
                    _uiState.update { it.copy(isBusy = false) }
                    setErrorMessage("Error: ${result.reason}")
                }
            }
        }
    }

    private fun processVoiceInput(text: String, isVoice: Boolean, isRetry: Boolean = false) {
        Log.d(TAG, "processVoiceInput: text='$text', isVoice=$isVoice, isRetry=$isRetry")
        _uiState.update {
            it.copy(
                inputText = "",
                isBusy = true,
                lastProcessedText = text,
                showRetryButton = false
            )
        }
        setErrorMessage(null)
        if (!isRetry) {
            appendMessage(text = text, isFromUser = true, isVoice = isVoice)
        }

        viewModelScope.launch {
            val lowercaseText = text.lowercase(Locale.ROOT)
            val reply = withTimeoutOrNull(5000) {
                when (lowercaseText) {
                    "hi" -> {
                        Log.d(TAG, "processVoiceInput: Handled as 'hi' command")
                        buildCapabilitiesReply()
                    }

                    else -> {
                        val task = parseAiTaskUseCase(text)
                        if (task == null) {
                            Log.d(
                                TAG,
                                "processVoiceInput: No on-device task, calling ExecuteOnlineAiTaskUseCase"
                            )
                            executeOnlineAiTaskUseCase(text)
                        } else {
                            Log.d(
                                TAG,
                                "processVoiceInput: Task parsed=$task, calling ExecuteOnDeviceAiTaskUseCase"
                            )
                            when (val result = executeOnDeviceAiTaskUseCase(task)) {
                                is AiExecutionResult.Success -> {
                                    Log.d(
                                        TAG,
                                        "processVoiceInput: On-device task succeeded. outputLength=${result.output.length}"
                                    )
                                    "Success (${result.taskLabel}):\n${result.output}"
                                }

                                is AiExecutionResult.Error -> {
                                    Log.e(
                                        TAG,
                                        "processVoiceInput: On-device task failed. reason=${result.reason}"
                                    )
                                    "Error: ${result.reason}"
                                }
                            }
                        }
                    }
                }
            }

            if (reply == null) {
                Log.w(TAG, "processVoiceInput: Task timed out after 5 seconds")
                _uiState.update { it.copy(showRetryButton = true, isBusy = false) }
                setErrorMessage("Request timed out. You can retry using the button below.")
                return@launch
            }

            Log.d(TAG, "processVoiceInput: Final reply generated. Length=${reply.length}")
            if (reply.startsWith("Error:")) {
                _uiState.update { it.copy(isBusy = false) }
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

    private fun buildRewriteTonePrompt(toneOptions: List<RewriteTone>): String {
        val lines = toneOptions.mapIndexed { index, tone ->
            "${index + 1}. ${tone.displayName()}"
        }

        return buildString {
            appendLine("Which rewrite type would you like to use?")
            lines.forEach { appendLine(it) }
            append("Reply with the number or tone name.")
        }
    }

    private fun parseRewriteToneSelection(
        selection: String,
        toneOptions: List<RewriteTone>
    ): RewriteTone? {
        val trimmedSelection = selection.trim()
        val selectedIndex = trimmedSelection.toIntOrNull()
        if (selectedIndex != null) {
            return toneOptions.getOrNull(selectedIndex - 1)
        }

        return toneOptions.firstOrNull { tone ->
            tone.displayName().equals(trimmedSelection, ignoreCase = true)
        }
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

    /** TODO: Future - replace string-based capability mapping with stable capability identifiers. */
    private fun mapCapabilityOption(capability: AiCapability): TextChatCapabilityOption? {
        val command = capability.command.lowercase(Locale.ROOT)
        return when {
            command.startsWith("summarize") -> TextChatCapabilityOption(
                capability = TextChatCapability.SUMMARIZE,
                title = "Summarize",
                description = capability.description
            )

            command.startsWith("proofread") -> TextChatCapabilityOption(
                capability = TextChatCapability.PROOFREAD,
                title = "Proofread",
                description = capability.description
            )

            command.startsWith("rewrite") -> TextChatCapabilityOption(
                capability = TextChatCapability.REWRITE,
                title = "Rewrite",
                description = capability.description
            )

            else -> null
        }
    }

    private fun RewriteTone.displayName(): String {
        val lowercaseName = name.lowercase(Locale.ROOT)
        return lowercaseName.replaceFirstChar { firstCharacter ->
            if (firstCharacter.isLowerCase()) {
                firstCharacter.titlecase(Locale.ROOT)
            } else {
                firstCharacter.toString()
            }
        }
    }
}
