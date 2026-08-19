package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class KeyboardRepository(
    private val context: Context,
    private val userWordDao: UserWordDao,
    private val clipboardDao: ClipboardDao
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("telugu_smart_prefs", Context.MODE_PRIVATE)

    private val _preferences = MutableStateFlow(loadPreferences())
    val preferences: StateFlow<KeyboardPreferences> = _preferences.asStateFlow()

    val userWords: Flow<List<UserWord>> = userWordDao.getAllUserWords()
    val clipboardSnippets: Flow<List<ClipboardSnippet>> = clipboardDao.getAllSnippets()

    private fun loadPreferences(): KeyboardPreferences {
        val themeName = prefs.getString("theme_type", KeyboardThemeType.MIDNIGHT_SLATE.name)
            ?: KeyboardThemeType.MIDNIGHT_SLATE.name
        val theme = try {
            KeyboardThemeType.valueOf(themeName)
        } catch (_: Exception) {
            KeyboardThemeType.MIDNIGHT_SLATE
        }

        return KeyboardPreferences(
            themeType = theme,
            keyHapticFeedback = prefs.getBoolean("key_haptic", true),
            keySoundFeedback = prefs.getBoolean("key_sound", false),
            keyBorderEnabled = prefs.getBoolean("key_border", true),
            keyCornerRadiusDp = prefs.getInt("key_corner_radius", 8),
            keyboardHeightPercent = prefs.getInt("keyboard_height", 100),
            autoCapitalization = prefs.getBoolean("auto_cap", true),
            doubleSpacePeriod = prefs.getBoolean("double_space_period", true),
            autoTransliterateOnSpace = prefs.getBoolean("auto_transliterate", true),
            showNumberRow = prefs.getBoolean("show_number_row", true),
            showPopupOnKeyPress = prefs.getBoolean("show_popup", true),
            sensitiveFieldProtection = prefs.getBoolean("sensitive_field_protection", true)
        )
    }

    fun updatePreferences(newPrefs: KeyboardPreferences) {
        prefs.edit().apply {
            putString("theme_type", newPrefs.themeType.name)
            putBoolean("key_haptic", newPrefs.keyHapticFeedback)
            putBoolean("key_sound", newPrefs.keySoundFeedback)
            putBoolean("key_border", newPrefs.keyBorderEnabled)
            putInt("key_corner_radius", newPrefs.keyCornerRadiusDp)
            putInt("keyboard_height", newPrefs.keyboardHeightPercent)
            putBoolean("auto_cap", newPrefs.autoCapitalization)
            putBoolean("double_space_period", newPrefs.doubleSpacePeriod)
            putBoolean("auto_transliterate", newPrefs.autoTransliterateOnSpace)
            putBoolean("show_number_row", newPrefs.showNumberRow)
            putBoolean("show_popup", newPrefs.showPopupOnKeyPress)
            putBoolean("sensitive_field_protection", newPrefs.sensitiveFieldProtection)
            apply()
        }
        _preferences.value = newPrefs
    }

    suspend fun insertOrUpdateWord(word: String, phonetic: String) {
        val existing = userWordDao.getWordsForPhonetic(phonetic.lowercase().trim())
        val found = existing.firstOrNull { it.teluguWord == word }
        if (found != null) {
            userWordDao.insertOrUpdate(
                found.copy(
                    frequency = found.frequency + 1,
                    lastUsedTimestamp = System.currentTimeMillis()
                )
            )
        } else {
            userWordDao.insertOrUpdate(
                UserWord(
                    teluguWord = word,
                    englishPhonetic = phonetic.lowercase().trim(),
                    frequency = 1,
                    lastUsedTimestamp = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun deleteWord(word: UserWord) {
        userWordDao.delete(word)
    }

    suspend fun deleteWordById(id: Long) {
        userWordDao.deleteById(id)
    }

    suspend fun addClipboardText(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return

        // Detect OTP (4-8 digits)
        val isOtp = trimmed.matches(Regex("^[0-9]{4,8}$"))
        // Detect Phone number
        val isPhone = trimmed.matches(Regex("^(\\+91|0)?[6-9]\\d{9}$"))
        val isUrl = trimmed.startsWith("http://") || trimmed.startsWith("https://")

        val detected = when {
            isOtp -> "OTP"
            isPhone -> "PHONE"
            isUrl -> "URL"
            else -> "TEXT"
        }

        clipboardDao.insertSnippet(
            ClipboardSnippet(
                content = trimmed,
                isPinned = false,
                isOtpOrPhone = isOtp || isPhone,
                detectedType = detected,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun togglePinClipboard(snippet: ClipboardSnippet) {
        clipboardDao.updateSnippet(snippet.copy(isPinned = !snippet.isPinned))
    }

    suspend fun deleteClipboardSnippet(snippet: ClipboardSnippet) {
        clipboardDao.deleteSnippet(snippet)
    }

    suspend fun deleteClipboardById(id: Long) {
        clipboardDao.deleteById(id)
    }

    suspend fun clearUnpinnedClipboard() {
        clipboardDao.clearUnpinned()
    }
}
