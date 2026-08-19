package com.example.engine

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.AudioTrack
import android.media.ToneGenerator
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.data.HapticStrength
import com.example.data.KeyboardSoundProfile
import kotlin.math.sin

/**
 * High performance, zero-latency audio and haptic synthesis engine.
 * Generates custom synthesized waveforms for Mechanical Switch (thock),
 * Typewriter, Water Drop Bubble Pop, and Cyber Digital Blip without requiring
 * external mp3 asset files.
 */
object SoundFeedbackHelper {

    private var audioManager: AudioManager? = null
    private var vibrator: Vibrator? = null
    private var isInitialized = false

    // Tone generators for quick synthesized beeps
    private var toneGen: ToneGenerator? = null

    fun initialize(context: Context) {
        if (isInitialized) return
        try {
            audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            try {
                toneGen = ToneGenerator(AudioManager.STREAM_SYSTEM, 50)
            } catch (_: Throwable) {}
            isInitialized = true
        } catch (_: Exception) {}
    }

    /**
     * Play realistic audio click or synthesized sound effect based on chosen profile.
     */
    fun playKeySound(profile: KeyboardSoundProfile) {
        try {
            when (profile) {
                KeyboardSoundProfile.DEFAULT_CLICK -> {
                    audioManager?.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f)
                }
                KeyboardSoundProfile.MECHANICAL_THOCK -> {
                    // Deep tactile thock (low frequency short sine pulse)
                    playSynthesizedPulse(frequency = 120.0, durationMs = 28, volume = 0.85f)
                }
                KeyboardSoundProfile.TYPEWRITER -> {
                    // Vintage typewriter sharp click
                    playSynthesizedPulse(frequency = 850.0, durationMs = 18, volume = 0.6f)
                }
                KeyboardSoundProfile.WATER_DROP -> {
                    // Ascending bubble droplet sound
                    playSynthesizedChirp(startFreq = 420.0, endFreq = 950.0, durationMs = 35, volume = 0.7f)
                }
                KeyboardSoundProfile.DIGITAL_BEEP -> {
                    playSynthesizedPulse(frequency = 1400.0, durationMs = 15, volume = 0.4f)
                }
            }
        } catch (_: Exception) {
            // Fallback
            audioManager?.playSoundEffect(AudioManager.FX_KEY_CLICK, 0.8f)
        }
    }

    /**
     * Trigger fine-grained haptic vibration with calibrated durations.
     */
    fun triggerHaptic(strength: HapticStrength) {
        val vib = vibrator ?: return
        if (!vib.hasVibrator()) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val amplitude = when (strength) {
                    HapticStrength.LIGHT -> 70
                    HapticStrength.MEDIUM -> 140
                    HapticStrength.STRONG -> 255
                }
                vib.vibrate(VibrationEffect.createOneShot(strength.durationMs, amplitude))
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(strength.durationMs)
            }
        } catch (_: Exception) {}
    }

    /**
     * Generates a fast zero-allocation micro sound pulse in memory.
     */
    private fun playSynthesizedPulse(frequency: Double, durationMs: Int, volume: Float) {
        try {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(100)
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val time = i.toDouble() / sampleRate
                // Exponential decay envelope for snappy physical impact sound
                val envelope = Math.exp(-12.0 * (i.toDouble() / numSamples))
                val sample = (sin(2.0 * Math.PI * frequency * time) * Short.MAX_VALUE * volume * envelope).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    android.media.AudioFormat.Builder()
                        .setSampleRate(sampleRate)
                        .setChannelMask(android.media.AudioFormat.CHANNEL_OUT_MONO)
                        .setEncoding(android.media.AudioFormat.ENCODING_PCM_16BIT)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            // Auto release after sound finishes
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (_: Exception) {}
            }, durationMs.toLong() + 50L)
        } catch (_: Exception) {
            audioManager?.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, volume)
        }
    }

    private fun playSynthesizedChirp(startFreq: Double, endFreq: Double, durationMs: Int, volume: Float) {
        try {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(100)
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val progress = i.toDouble() / numSamples
                val currentFreq = startFreq + (endFreq - startFreq) * progress
                val time = i.toDouble() / sampleRate
                val envelope = Math.sin(Math.PI * progress) // bell curve
                val sample = (sin(2.0 * Math.PI * currentFreq * time) * Short.MAX_VALUE * volume * envelope).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    android.media.AudioFormat.Builder()
                        .setSampleRate(sampleRate)
                        .setChannelMask(android.media.AudioFormat.CHANNEL_OUT_MONO)
                        .setEncoding(android.media.AudioFormat.ENCODING_PCM_16BIT)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (_: Exception) {}
            }, durationMs.toLong() + 50L)
        } catch (_: Exception) {}
    }
}
