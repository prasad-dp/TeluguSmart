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

data class KeyboardPreferences(
    val themeType: KeyboardThemeType = KeyboardThemeType.MIDNIGHT_SLATE,
    val keyHapticFeedback: Boolean = true,
    val keySoundFeedback: Boolean = false,
    val keyBorderEnabled: Boolean = true,
    val keyCornerRadiusDp: Int = 8,
    val keyboardHeightPercent: Int = 100, // 80% to 120%
    val autoCapitalization: Boolean = true,
    val doubleSpacePeriod: Boolean = true,
    val autoTransliterateOnSpace: Boolean = true,
    val showNumberRow: Boolean = true,
    val showPopupOnKeyPress: Boolean = true,
    val sensitiveFieldProtection: Boolean = true,
    val customPhotoPreset: String? = null, // e.g. "tirupati_gold", "godavari_sunrise", "charminar_night", "araku_valley"
    val customPhotoUri: String? = null,
    val customPhotoDarkness: Float = 0.50f, // 0.1f to 0.9f
    val keyOpacity: Float = 0.90f // 0.4f to 1.0f
)
