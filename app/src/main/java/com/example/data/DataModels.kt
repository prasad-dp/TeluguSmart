package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_words")
data class UserWord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val teluguWord: String,
    val englishPhonetic: String,
    val frequency: Int = 1,
    val lastUsedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "clipboard_snippets")
data class ClipboardSnippet(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    val isPinned: Boolean = false,
    val isOtpOrPhone: Boolean = false,
    val detectedType: String = "TEXT", // "OTP", "PHONE", "URL", "TEXT"
    val timestamp: Long = System.currentTimeMillis()
)

enum class KeyboardThemeType(
    val displayName: String,
    val description: String,
    val category: String = "Classic Solid"
) {
    // Top Solid & System Dark Themes
    PITCH_BLACK("Pitch Black (AMOLED)", "True deep #000000 black canvas with sharp contrast keys & cyan accent", "Solid & Dark"),
    MIDNIGHT_SLATE("Midnight Slate (Dark)", "Modern dark navy slate with vibrant royal blue accents (Default)", "Solid & Dark"),
    CLEAN_LIGHT("Porcelain Light", "Clean crisp white canvas with slate glyphs & cobalt highlights", "Solid & Dark"),
    DESH_ROYAL_BLUE("Sapphire Indigo", "Classic Telugu royal blue with electric azure enter key", "Solid & Dark"),
    SAFFRON_ORANGE("Saffron & Amber", "Festive Telugu saffron with warm golden ember accents", "Solid & Dark"),
    EMERALD_GREEN("Andhra Emerald", "Soothing dark botanical pine with vibrant mint green keycaps", "Solid & Dark"),

    // High Contrast & Market Favorite Themes
    CHERRY_BLOSSOM_WHITE("White Cherry Contrast", "Pure porcelain white keys with deep cherry crimson letters & ruby accents", "High Contrast"),
    MATCHA_CREAM("Matcha Cream", "Warm ivory canvas with rich dark matcha green letters & sage accents", "High Contrast"),
    COFFEE_ESPRESSO("Caramel Espresso", "Warm creamy latte surface with deep roasted espresso dark typography", "High Contrast"),
    CYBER_NEON_CONTRAST("Cyber Neon Glow", "Obsidian night canvas with high-visibility neon gold & magenta typography", "High Contrast"),
    NORDIC_GLACIER("Nordic Glacier", "Deep arctic navy surface with crystal ice-cyan illuminated glyphs", "High Contrast")
}

enum class KeyboardEffectType(
    val id: String,
    val displayName: String,
    val description: String
) {
    NONE("none", "None", "Clean classic key tap"),
    MORPH_LIGATURE("morph_ligature", "Morph & Ligature", "Telugu stroke reveal & glyph expansion (<4ms)"),
    HAPTIC_3D_DEPTH("haptic_3d", "Haptic-Sync 3D", "3D perspective depth tilt & tactile reflection (<2ms)"),
    GLASS_BLOOM("glass_bloom", "Glassmorphic Bloom", "Frosted glass halo burst & specular specular bloom (<6ms)"),
    SHADER_WAVE("shader_wave", "Reactive Shader Wave", "Sine-wave shockwave sweep across key matrix (<8ms)"),
    MICRO_PARTICLES("micro_particles", "Micro-Particle Emitter", "Zero-allocation physics particle explosion (<8ms)"),
    CYBER_CYAN("cyber_cyan", "Cyan Aurora", "Soft glowing cyan & mint aura with halo bloom"),
    AMBER_FIRE("amber_fire", "Amber Glow", "Warm golden ember glow with flame sparks"),
    NEON_GREEN("neon_green", "Emerald Neon", "Electric neon green outline with pulse aura"),
    MAGENTA_BURST("magenta_burst", "Magenta Glow", "Vibrant cyber pink & magenta energy pulse"),
    RAINBOW_WAVE("rainbow_wave", "RGB Chroma", "Dynamic full-spectrum chromatic wave burst"),
    SPARKLE_STARS("sparkle_stars", "Sparkle Stars", "Golden celestial twinkling star particles"),
    WATER_RIPPLE("water_ripple", "Water Ripple", "Fluid liquid droplet concentric ripples"),
    HEART_POP("heart_pop", "Floating Hearts", "Romantic floating mini heart particles")
}

enum class KeyboardSoundProfile(
    val id: String,
    val displayName: String,
    val description: String
) {
    DEFAULT_CLICK("default", "Standard Click", "Crisp subtle Android key click"),
    MECHANICAL_THOCK("mechanical", "Mechanical Switch", "Deep satisfying tactile thock switch"),
    TYPEWRITER("typewriter", "Classic Typewriter", "Vintage mechanical typewriter click & clack"),
    WATER_DROP("water_drop", "Water Bubble Pop", "Soothing aquatic liquid drop pop"),
    DIGITAL_BEEP("digital", "Cyber Blip", "Futuristic short digital sound")
}

enum class OneHandedMode(
    val id: String,
    val displayName: String
) {
    OFF("off", "Full Width"),
    LEFT("left", "Docked Left (Left-Handed)"),
    RIGHT("right", "Docked Right (Right-Handed)")
}

enum class HapticStrength(
    val id: String,
    val displayName: String,
    val durationMs: Long
) {
    LIGHT("light", "Light (Gentle)", 12L),
    MEDIUM("medium", "Medium (Balanced)", 25L),
    STRONG("strong", "Strong (Crisp)", 45L)
}

data class KeyboardPreferences(
    val themeType: KeyboardThemeType = KeyboardThemeType.MIDNIGHT_SLATE,
    val activeEffect: KeyboardEffectType = KeyboardEffectType.CYBER_CYAN,
    val keyHapticFeedback: Boolean = true,
    val hapticStrength: HapticStrength = HapticStrength.MEDIUM,
    val keySoundFeedback: Boolean = false,
    val soundProfile: KeyboardSoundProfile = KeyboardSoundProfile.DEFAULT_CLICK,
    val keyBorderEnabled: Boolean = true,
    val keyCornerRadiusDp: Int = 8,
    val keyboardHeightPercent: Int = 100, // 80% to 120%
    val autoCapitalization: Boolean = true,
    val doubleSpacePeriod: Boolean = true,
    val autoTransliterateOnSpace: Boolean = true,
    val showNumberRow: Boolean = true,
    val showPopupOnKeyPress: Boolean = true,
    val sensitiveFieldProtection: Boolean = true,
    val oneHandedMode: OneHandedMode = OneHandedMode.OFF,
    val isFloatingKeyboard: Boolean = false,
    val glideTypingEnabled: Boolean = true,
    val nextWordPredictionEnabled: Boolean = true,
    val customPhotoPreset: String? = null, // e.g. "tirupati_gold", "godavari_sunrise", "charminar_night", "araku_valley"
    val customPhotoUri: String? = null,
    val customPhotoDarkness: Float = 0.50f, // 0.1f to 0.9f
    val keyOpacity: Float = 0.90f, // 0.4f to 1.0f
    val customAccentColor: Long? = null
)
