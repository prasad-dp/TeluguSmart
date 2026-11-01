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
    NORDIC_GLACIER("Nordic Glacier", "Deep arctic navy surface with crystal ice-cyan illuminated glyphs", "High Contrast"),
    MECHANICAL_GLOW("Mechanical Glow (Cyber)", "Desh-style deep obsidian keyboard with electric cyan circuit key outlines", "High Contrast")
}

enum class KeyboardEffectType(
    val id: String,
    val displayName: String,
    val description: String
) {
    NONE("none", "None", "Clean classic key tap"),
    CYBER_CYAN("cyber_cyan", "Cyan Aurora (Electric Blue)", "Desh-style electric cyan blue key highlight pulse (<4ms)"),
    NEON_GREEN("neon_green", "Emerald Neon Pulse", "Desh-style electric neon green border pulse (<4ms)")
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
    val keySoundVolume: Float = 0.85f,
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
