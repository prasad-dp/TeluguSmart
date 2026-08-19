package com.example.engine

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

/**
 * Text-to-Speech Helper for providing crystal clear spoken voice guidance at each setup step.
 * Supports auto-play with pending queue if TTS takes a moment to initialize.
 */
class VoiceInstructionHelper(
    context: Context,
    private val onInitReady: (() -> Unit)? = null
) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var pendingSpeak: Pair<String, String>? = null

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                val teluguLocale = Locale.forLanguageTag("te-IN")
                val langResult = tts?.setLanguage(teluguLocale)
                if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts?.language = Locale.forLanguageTag("en-IN")
                }
                tts?.setSpeechRate(0.95f)
                tts?.setPitch(1.0f)

                // Execute any queued auto-play speech
                pendingSpeak?.let { (telugu, english) ->
                    pendingSpeak = null
                    speak(telugu, english)
                }
                onInitReady?.invoke()
            } else {
                Log.e("VoiceInstructionHelper", "TTS Initialization failed")
            }
        }
    }

    fun speak(textTelugu: String, textEnglishFallback: String) {
        if (!isInitialized) {
            pendingSpeak = Pair(textTelugu, textEnglishFallback)
            return
        }
        tts?.stop()
        val teluguLocale = Locale.forLanguageTag("te-IN")
        val isTeluguSupported = tts?.isLanguageAvailable(teluguLocale) ?: TextToSpeech.LANG_NOT_SUPPORTED

        val textToSpeak = if (isTeluguSupported >= TextToSpeech.LANG_AVAILABLE) {
            tts?.language = teluguLocale
            textTelugu
        } else {
            tts?.language = Locale.forLanguageTag("en-IN")
            textEnglishFallback
        }

        tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "step_guidance_${System.currentTimeMillis()}")
    }

    fun stop() {
        pendingSpeak = null
        tts?.stop()
    }

    fun shutdown() {
        pendingSpeak = null
        tts?.stop()
        tts?.shutdown()
    }
}
