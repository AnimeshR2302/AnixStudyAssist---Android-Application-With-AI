package com.anix.android.anixstudyassist.aichat.presentation.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoiceManager @Inject constructor(
    @ApplicationContext private val context: Context
) : RecognitionListener {

    companion object {
        private const val TAG = "ANIX_VoiceManager"
    }

    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady = false
    private var isListening = false
    private var prefersOnDeviceRecognition = false

    private var onResult: ((String) -> Unit)? = null
    private var onError: ((String) -> Unit)? = null

    init {
        initializeTts()
    }

    private fun initializeTts() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.getDefault()
                isTtsReady = true
            } else {
                Log.e(TAG, "TTS Initialization failed")
            }
        }
    }

    fun startListening(onResult: (String) -> Unit, onError: (String) -> Unit) {
        this.onResult = onResult
        this.onError = onError

        val hasPlatformRecognizer = SpeechRecognizer.isRecognitionAvailable(context)
        val hasOnDeviceRecognizer = SpeechRecognizer.isOnDeviceRecognitionAvailable(context)
        if (!hasPlatformRecognizer && !hasOnDeviceRecognizer) {
            onError("Speech recognition not available on this device")
            clearCallbacks()
            return
        }

        if (isListening) {
            speechRecognizer?.cancel()
            isListening = false
        }

        if (speechRecognizer == null) {
            speechRecognizer = createRecognizer(hasOnDeviceRecognizer)
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, prefersOnDeviceRecognition)
        }

        try {
            isListening = true
            speechRecognizer?.startListening(intent)
        } catch (error: Throwable) {
            Log.e(TAG, "startListening failed", error)
            isListening = false
            onError("Could not start speech recognition. Try again.")
            clearCallbacks()
        }
    }

    fun stopListening() {
        isListening = false
        speechRecognizer?.stopListening()
    }

    private fun createRecognizer(hasOnDeviceRecognizer: Boolean): SpeechRecognizer {
        prefersOnDeviceRecognition = hasOnDeviceRecognizer
        val recognizer = if (hasOnDeviceRecognizer) {
            Log.d(TAG, "Using on-device speech recognizer")
            SpeechRecognizer.createOnDeviceSpeechRecognizer(context)
        } else {
            Log.d(TAG, "Using platform speech recognizer")
            SpeechRecognizer.createSpeechRecognizer(context)
        }
        return recognizer.apply {
            setRecognitionListener(this@VoiceManager)
        }
    }

    private fun clearCallbacks() {
        onResult = null
        onError = null
    }

    fun speak(text: String) {
        if (textToSpeech == null) {
            initializeTts()
        }
        if (isTtsReady) {
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun shutdown() {
        isListening = false
        speechRecognizer?.destroy()
        speechRecognizer = null
        textToSpeech?.shutdown()
        textToSpeech = null
        isTtsReady = false
    }

    // RecognitionListener Callbacks
    override fun onReadyForSpeech(params: Bundle?) {
        Log.d(TAG, "Ready for speech")
    }

    override fun onBeginningOfSpeech() {
        Log.d(TAG, "Beginning of speech")
    }

    override fun onRmsChanged(rmsdB: Float) {}
    override fun onBufferReceived(buffer: ByteArray?) {}
    override fun onEndOfSpeech() {
        Log.d(TAG, "End of speech")
    }

    override fun onError(error: Int) {
        val errorMessage = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No match"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
            SpeechRecognizer.ERROR_SERVER -> "Error from server"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
            else -> "Unknown error"
        }
        Log.e(TAG, "onError: $errorMessage ($error)")
        isListening = false
        onError?.invoke(errorMessage)
        clearCallbacks()
    }

    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        Log.d(TAG, "onResults: matchesCount=${matches?.size ?: 0}")
        if (!matches.isNullOrEmpty()) {
            val bestMatch = matches[0]
            Log.d(TAG, "onResults: bestMatch='$bestMatch'")
            onResult?.invoke(bestMatch)
        } else {
            onError?.invoke("No speech recognized. Try again.")
        }
        isListening = false
        clearCallbacks()
    }

    override fun onPartialResults(partialResults: Bundle?) {}
    override fun onEvent(eventType: Int, params: Bundle?) {}
}
