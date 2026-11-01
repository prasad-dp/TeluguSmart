package com.example.engine

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.data.HapticStrength
import com.example.data.KeyboardSoundProfile
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * High performance, zero-latency audio and haptic synthesis engine.
 * Uses pre-allocated static AudioTrack pools to eliminate allocation overhead,
 * audio track leaks, and track exhaustion while providing rich audible volume.
 */
object SoundFeedbackHelper {

    private var audioManager: AudioManager? = null
    private var vibrator: Vibrator? = null
    private var isInitialized = false

    private const val SAMPLE_RATE = 24000
    private val profileTracks = mutableMapOf<KeyboardSoundProfile, Array<AudioTrack>>()
    private val trackRoundRobin = mutableMapOf<KeyboardSoundProfile, Int>()

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

            // Pre-initialize static AudioTracks for all sound profiles
            initAudioTracks()

            isInitialized = true
        } catch (_: Throwable) {}
    }

    private fun initAudioTracks() {
        try {
            KeyboardSoundProfile.entries.forEach { profile ->
                val buffer = generateProfileBuffer(profile)
                if (buffer.isNotEmpty()) {
                    val tracks = mutableListOf<AudioTrack>()
                    for (i in 0 until 2) {
                        try {
                            val track = createStaticTrack(buffer)
                            tracks.add(track)
                        } catch (_: Throwable) {}
                    }
                    if (tracks.isNotEmpty()) {
                        profileTracks[profile] = tracks.toTypedArray()
                        trackRoundRobin[profile] = 0
                    }
                }
            }
        } catch (_: Throwable) {}
    }

    private fun createStaticTrack(buffer: ShortArray): AudioTrack {
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .build()
            )
            .setBufferSizeInBytes(buffer.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(buffer, 0, buffer.size)
        return audioTrack
    }

    /**
     * Synthesizes punchy, crisp, rich sound waveforms for each profile.
     */
    private fun generateProfileBuffer(profile: KeyboardSoundProfile): ShortArray {
        return when (profile) {
            KeyboardSoundProfile.DEFAULT_CLICK -> {
                // Crisp click transient (1200Hz to 600Hz snap, 22ms)
                val durationMs = 22
                val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val progress = i.toDouble() / numSamples
                    val freq = 1200.0 - 600.0 * progress
                    val time = i.toDouble() / SAMPLE_RATE
                    val env = exp(-7.0 * progress)
                    val s = sin(2.0 * PI * freq * time) * Short.MAX_VALUE * 0.95 * env
                    buffer[i] = s.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }
                buffer
            }
            KeyboardSoundProfile.MECHANICAL_THOCK -> {
                // Deep acoustic tactile switch thock (110Hz body + 360Hz click transient, 36ms)
                val durationMs = 36
                val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val progress = i.toDouble() / numSamples
                    val time = i.toDouble() / SAMPLE_RATE
                    val clickEnv = exp(-18.0 * progress)
                    val click = sin(2.0 * PI * 360.0 * time) * clickEnv * 0.5
                    val bodyEnv = exp(-5.0 * progress)
                    val body = (sin(2.0 * PI * 115.0 * time) * 0.8 + sin(2.0 * PI * 230.0 * time) * 0.4) * bodyEnv
                    val s = (click + body) * Short.MAX_VALUE * 0.95
                    buffer[i] = s.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }
                buffer
            }
            KeyboardSoundProfile.TYPEWRITER -> {
                // Metallic typewriter striker clack (1800Hz transient + 750Hz metallic ring, 28ms)
                val durationMs = 28
                val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val progress = i.toDouble() / numSamples
                    val time = i.toDouble() / SAMPLE_RATE
                    val strike = sin(2.0 * PI * 1800.0 * time) * exp(-20.0 * progress) * 0.6
                    val clatter = sin(2.0 * PI * 750.0 * time) * exp(-6.5 * progress) * 0.7
                    val s = (strike + clatter) * Short.MAX_VALUE * 0.95
                    buffer[i] = s.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }
                buffer
            }
            KeyboardSoundProfile.WATER_DROP -> {
                // Resonant bubble pop chirp (450Hz -> 1050Hz, 38ms)
                val durationMs = 38
                val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val progress = i.toDouble() / numSamples
                    val freq = 450.0 + 600.0 * progress
                    val time = i.toDouble() / SAMPLE_RATE
                    val env = sin(PI * progress)
                    val s = sin(2.0 * PI * freq * time) * Short.MAX_VALUE * 0.95 * env
                    buffer[i] = s.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }
                buffer
            }
            KeyboardSoundProfile.DIGITAL_BEEP -> {
                // Futuristic cyber pulse blip (1350Hz, 20ms)
                val durationMs = 20
                val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val progress = i.toDouble() / numSamples
                    val time = i.toDouble() / SAMPLE_RATE
                    val env = exp(-8.0 * progress)
                    val s = (sin(2.0 * PI * 1350.0 * time) * 0.8 + sin(2.0 * PI * 2700.0 * time) * 0.2) * Short.MAX_VALUE * 0.95 * env
                    buffer[i] = s.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }
                buffer
            }
        }
    }

    /**
     * Play key sound using pre-allocated static AudioTracks with volume control.
     */
    fun playKeySound(profile: KeyboardSoundProfile, volumeFraction: Float = 0.85f) {
        val safeVolume = volumeFraction.coerceIn(0.05f, 1.0f)

        try {
            val tracks = profileTracks[profile]
            if (tracks != null && tracks.isNotEmpty()) {
                val idx = (trackRoundRobin[profile] ?: 0) % tracks.size
                trackRoundRobin[profile] = idx + 1
                val track = tracks[idx]

                if (track.state == AudioTrack.STATE_INITIALIZED) {
                    track.stop()
                    track.reloadStaticData()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        track.setVolume(safeVolume)
                    } else {
                        @Suppress("DEPRECATION")
                        track.setStereoVolume(safeVolume, safeVolume)
                    }
                    track.play()
                    return
                }
            }
        } catch (_: Throwable) {}

        // Fallback to system sound effect
        try {
            audioManager?.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, safeVolume)
        } catch (_: Throwable) {}
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
}
