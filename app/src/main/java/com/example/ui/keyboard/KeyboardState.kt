package com.example.ui.keyboard

enum class KeyboardLayoutMode(val displayName: String, val shortName: String) {
    TENGLISH("Telugu (Tenglish)", "తె-EN"),
    ENGLISH("English (QWERTY)", "EN"),
    NATIVE_TELUGU("Telugu (Native)", "తెలుగు"),
    HANDWRITING("Handwriting (వ్రాత)", "వ్రాత"),
    SYMBOLS("Numbers & Symbols", "?123"),
    MORE_SYMBOLS("More Symbols", "=/<")
}

enum class KeyboardPanel {
    NONE,
    DPAD,
    CLIPBOARD,
    STICKERS,
    CUSTOM_STICKER_MAKER,
    GIF_PICKER,
    GREETINGS_WISHES,
    FANCY_FONTS,
    EMOJI_PICKER,
    TRANSLATOR,
    APP_SEARCH,
    VOICE,
    GEN_AI_TONE,
    SMART_REPLY
}

data class KeyAction(
    val primaryText: String,
    val secondaryText: String? = null,
    val isSpecial: Boolean = false,
    val weight: Float = 1.0f,
    val code: KeyCode = KeyCode.CHARACTER
)

enum class KeyCode {
    CHARACTER,
    SHIFT,
    BACKSPACE,
    SPACE,
    ENTER,
    MODE_SWITCH,
    LANGUAGE_SWITCH,
    DPAD_TOGGLE,
    CLIPBOARD_TOGGLE,
    STICKER_TOGGLE,
    GIF_TOGGLE,
    APP_SEARCH_TOGGLE,
    VOICE_TOGGLE,
    AI_TONE_TOGGLE,
    DISMISS_KEYBOARD
}
