package com.example.ui.keyboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.inputmethod.InputConnection
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ClipboardSnippet
import com.example.data.KeyboardEffectType
import com.example.data.KeyboardPreferences
import com.example.data.OneHandedMode
import com.example.engine.SoundFeedbackHelper
import com.example.engine.TeluguTransliterationEngine
import com.example.model.TeluguMemeSticker
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Arrangement
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun KeyboardRootView(
    modifier: Modifier = Modifier,
    preferences: KeyboardPreferences,
    inputConnection: InputConnection? = null,
    // For embedded sandbox testpad
    currentBufferText: String = "",
    onBufferTextChange: ((String) -> Unit)? = null,
    clipboardSnippets: List<ClipboardSnippet> = emptyList(),
    onSaveClipboardText: ((String) -> Unit)? = null,
    onTogglePinClipboard: ((ClipboardSnippet) -> Unit)? = null,
    onDeleteClipboard: ((ClipboardSnippet) -> Unit)? = null,
    onClearUnpinnedClipboard: (() -> Unit)? = null,
    onLearnUserWord: ((String, String) -> Unit)? = null,
    onDismissKeyboard: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val palette = remember(preferences.themeType, preferences.customAccentColor) {
        KeyboardThemeColors.getPalette(preferences.themeType, preferences.customAccentColor)
    }

    var layoutMode by remember { mutableStateOf(KeyboardLayoutMode.TENGLISH) }
    var activePanel by remember { mutableStateOf(KeyboardPanel.NONE) }
    var isShifted by remember { mutableStateOf(false) }
    var isMoreSymbols by remember { mutableStateOf(false) }
    var showChangeKeyboardDialog by remember { mutableStateOf(false) }
    var oneHandedMode by remember(preferences.oneHandedMode) { mutableStateOf(preferences.oneHandedMode) }

    LaunchedEffect(Unit) {
        SoundFeedbackHelper.initialize(context)
    }

    // Guninthalu Popover state
    var selectedConsonantForPopup by remember { mutableStateOf<String?>(null) }

    // Live Key Tap Effect trigger timestamp
    var lastKeyTapTime by remember { mutableStateOf(0L) }
    val triggerKeyEffect = {
        if (preferences.activeEffect != KeyboardEffectType.NONE) {
            lastKeyTapTime = System.currentTimeMillis()
        }
        if (preferences.keySoundFeedback) {
            SoundFeedbackHelper.playKeySound(preferences.soundProfile)
        }
        if (preferences.keyHapticFeedback) {
            SoundFeedbackHelper.triggerHaptic(preferences.hapticStrength)
        }
    }

    // Internal typing buffer for Tenglish transliteration
    var activeComposingToken by remember { mutableStateOf("") }
    var previousCommittedWord by remember { mutableStateOf<String?>(null) }

    // Suggestions derived from active token
    val (teluguSug, engSug, altSug) = remember(activeComposingToken, previousCommittedWord) {
        if (layoutMode == KeyboardLayoutMode.TENGLISH) {
            TeluguTransliterationEngine.transliterate(activeComposingToken, previousCommittedWord)
        } else {
            Triple("", "", "")
        }
    }

    // Helper to send text to InputConnection or local sandbox callback
    val commitTextInternal = { text: String ->
        if (inputConnection != null) {
            inputConnection.commitText(text, 1)
        } else if (onBufferTextChange != null) {
            onBufferTextChange(currentBufferText + text)
        }
    }

    val deleteBackInternal = {
        if (inputConnection != null) {
            inputConnection.deleteSurroundingText(1, 0)
        } else if (onBufferTextChange != null) {
            if (currentBufferText.isNotEmpty()) {
                onBufferTextChange(currentBufferText.dropLast(1))
            }
        }
    }

    val deleteWordInternal = {
        if (inputConnection != null) {
            val textBefore = inputConnection.getTextBeforeCursor(30, 0)?.toString() ?: ""
            val lastSpace = textBefore.trimEnd().lastIndexOfAny(charArrayOf(' ', '\n', '.', ','))
            val toDelete = if (lastSpace != -1) textBefore.length - lastSpace else textBefore.length
            if (toDelete > 0) {
                inputConnection.deleteSurroundingText(toDelete, 0)
            }
        } else if (onBufferTextChange != null) {
            val trimmed = currentBufferText.trimEnd()
            val lastSpace = trimmed.lastIndexOfAny(charArrayOf(' ', '\n', '.', ','))
            if (lastSpace != -1) {
                onBufferTextChange(trimmed.substring(0, lastSpace + 1))
            } else {
                onBufferTextChange("")
            }
        }
    }

    val moveCursorInternal = { delta: Int ->
        if (inputConnection != null) {
            // Delta cursor shift
            val selected = inputConnection.getSelectedText(0)?.length ?: 0
            // Shift by step
            inputConnection.commitCorrection(null)
        }
    }

    // Speech recognition setup for Voice Typing
    var isListeningVoice by remember { mutableStateOf(false) }
    var speechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            speechRecognizer?.destroy()
        }
    }

    val startVoiceRecognition = {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer = recognizer
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "te-IN")
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "te-IN")
                putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, "te-IN")
                putExtra(RecognizerIntent.EXTRA_PROMPT, "తెలుగులో మాట్లాడండి (Speak in Telugu)...")
            }

            recognizer.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) { isListeningVoice = true }
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() { isListeningVoice = false }
                override fun onError(error: Int) { isListeningVoice = false }
                override fun onResults(results: Bundle?) {
                    isListeningVoice = false
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        val spoken = matches[0] + " "
                        commitTextInternal(spoken)
                        previousCommittedWord = matches[0]
                    }
                }
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
            recognizer.startListening(intent)
        }
    }

    KeyboardBackgroundSurface(
        presetId = preferences.customPhotoPreset,
        customPhotoUri = preferences.customPhotoUri,
        customDarkness = preferences.customPhotoDarkness,
        basePalette = palette,
        modifier = modifier.fillMaxWidth().wrapContentHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
        // Suggestion strip and top utility toolbar
        SuggestionStrip(
            palette = palette,
            layoutMode = layoutMode,
            currentWord = activeComposingToken,
            teluguSuggestion = teluguSug,
            englishSuggestion = engSug,
            alternateSuggestion = altSug,
            activePanel = activePanel,
            onSelectSuggestion = { selectedWord ->
                commitTextInternal(selectedWord + " ")
                if (activeComposingToken.isNotEmpty()) {
                    onLearnUserWord?.invoke(selectedWord, activeComposingToken)
                }
                previousCommittedWord = selectedWord
                activeComposingToken = ""
            },
            onTogglePanel = { panel ->
                if (panel == KeyboardPanel.VOICE) {
                    startVoiceRecognition()
                }
                activePanel = panel
                selectedConsonantForPopup = null
            },
            onSwitchLayout = {
                layoutMode = when (layoutMode) {
                    KeyboardLayoutMode.TENGLISH -> KeyboardLayoutMode.ENGLISH
                    KeyboardLayoutMode.ENGLISH -> KeyboardLayoutMode.NATIVE_TELUGU
                    KeyboardLayoutMode.NATIVE_TELUGU -> KeyboardLayoutMode.TENGLISH
                    else -> KeyboardLayoutMode.TENGLISH
                }
            }
        )

        // Overlay Panels
        Box(modifier = Modifier.fillMaxWidth()) {
            when (activePanel) {
                KeyboardPanel.DPAD -> {
                    TextEditingDpad(
                        palette = palette,
                        onMoveCursorLeft = { moveCursorInternal(-1) },
                        onMoveCursorRight = { moveCursorInternal(1) },
                        onMoveCursorUp = { moveCursorInternal(-10) },
                        onMoveCursorDown = { moveCursorInternal(10) },
                        onMoveToStart = { moveCursorInternal(-999) },
                        onMoveToEnd = { moveCursorInternal(999) },
                        onSelectAll = { inputConnection?.performContextMenuAction(android.R.id.selectAll) },
                        onCut = { inputConnection?.performContextMenuAction(android.R.id.cut) },
                        onCopy = { inputConnection?.performContextMenuAction(android.R.id.copy) },
                        onPaste = { inputConnection?.performContextMenuAction(android.R.id.paste) },
                        onClose = { activePanel = KeyboardPanel.NONE }
                    )
                }

                KeyboardPanel.CLIPBOARD -> {
                    ClipboardSheet(
                        palette = palette,
                        snippets = clipboardSnippets,
                        onSelectSnippet = { text ->
                            commitTextInternal(text)
                            activePanel = KeyboardPanel.NONE
                        },
                        onTogglePin = { snippet -> onTogglePinClipboard?.invoke(snippet) },
                        onDeleteSnippet = { snippet -> onDeleteClipboard?.invoke(snippet) },
                        onClearUnpinned = { onClearUnpinnedClipboard?.invoke() },
                        onClose = { activePanel = KeyboardPanel.NONE }
                    )
                }

                KeyboardPanel.STICKERS -> {
                    StickerSheet(
                        palette = palette,
                        onSelectSticker = { sticker ->
                            commitTextInternal("[${sticker.punchlineTelugu} ${sticker.emoji}] ")
                            activePanel = KeyboardPanel.NONE
                        },
                        onOpenCustomMaker = {
                            activePanel = KeyboardPanel.CUSTOM_STICKER_MAKER
                        },
                        onClose = { activePanel = KeyboardPanel.NONE }
                    )
                }

                KeyboardPanel.CUSTOM_STICKER_MAKER -> {
                    CustomStickerMakerSheet(
                        palette = palette,
                        initialText = currentBufferText.ifEmpty { activeComposingToken },
                        onCommitSticker = { stickerText ->
                            commitTextInternal(stickerText + " ")
                            activePanel = KeyboardPanel.NONE
                        },
                        onClose = { activePanel = KeyboardPanel.NONE }
                    )
                }

                KeyboardPanel.TRANSLATOR -> {
                    LiveTranslatorSheet(
                        palette = palette,
                        initialInput = currentBufferText.ifEmpty { activeComposingToken },
                        onCommitTranslation = { translated ->
                            commitTextInternal(translated)
                            activePanel = KeyboardPanel.NONE
                        },
                        onClose = { activePanel = KeyboardPanel.NONE }
                    )
                }

                KeyboardPanel.SMART_REPLY -> {
                    SmartReplySheet(
                        palette = palette,
                        currentText = currentBufferText.ifEmpty { previousCommittedWord ?: "" },
                        onSelectReply = { reply ->
                            commitTextInternal(reply + " ")
                            activePanel = KeyboardPanel.NONE
                        },
                        onClose = { activePanel = KeyboardPanel.NONE }
                    )
                }

                KeyboardPanel.GIF_PICKER -> {
                    GifPickerSheet(
                        palette = palette,
                        onSelectGif = { gif ->
                            commitTextInternal("[GIF: ${gif.captionTelugu} ${gif.emoji}] ")
                            activePanel = KeyboardPanel.NONE
                        },
                        onClose = { activePanel = KeyboardPanel.NONE }
                    )
                }

                KeyboardPanel.APP_SEARCH -> {
                    AppSearchSheet(
                        palette = palette,
                        onClose = { activePanel = KeyboardPanel.NONE }
                    )
                }

                KeyboardPanel.GREETINGS_WISHES -> {
                    GreetingsWishesSheet(
                        palette = palette,
                        onSelectPhrase = { phrase ->
                            commitTextInternal(phrase + " ")
                            activePanel = KeyboardPanel.NONE
                        },
                        onClose = { activePanel = KeyboardPanel.NONE }
                    )
                }

                KeyboardPanel.FANCY_FONTS -> {
                    FancyFontSheet(
                        palette = palette,
                        initialText = currentBufferText.ifEmpty { activeComposingToken.ifEmpty { "Telugu" } },
                        onSelectStyledText = { styledText ->
                            commitTextInternal(styledText + " ")
                            activePanel = KeyboardPanel.NONE
                        },
                        onClose = { activePanel = KeyboardPanel.NONE }
                    )
                }

                KeyboardPanel.EMOJI_PICKER -> {
                    EmojiPickerSheet(
                        palette = palette,
                        onSelectEmoji = { emoji ->
                            commitTextInternal(emoji)
                        },
                        onClose = { activePanel = KeyboardPanel.NONE }
                    )
                }

                KeyboardPanel.GEN_AI_TONE -> {
                    ToneRephraseSheet(
                        palette = palette,
                        currentText = currentBufferText.ifEmpty { previousCommittedWord ?: "బాగున్నారా" },
                        onApplyRephrase = { rephrased ->
                            commitTextInternal(rephrased + " ")
                            activePanel = KeyboardPanel.NONE
                        },
                        onClose = { activePanel = KeyboardPanel.NONE }
                    )
                }

                KeyboardPanel.VOICE -> {
                    // Quick inline voice status
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(palette.surface)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        VoiceTypingBanner(
                            isListening = isListeningVoice,
                            palette = palette,
                            onStop = {
                                speechRecognizer?.stopListening()
                                activePanel = KeyboardPanel.NONE
                            }
                        )
                    }
                }

                KeyboardPanel.NONE -> {
                    // Render Main Keyboards with One-Handed Mode support
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (oneHandedMode == OneHandedMode.RIGHT) {
                            OneHandedSideBar(
                                palette = palette,
                                onExpand = { oneHandedMode = OneHandedMode.OFF },
                                onSwitchSide = { oneHandedMode = OneHandedMode.LEFT },
                                modifier = Modifier.width(48.dp)
                            )
                        }

                        Box(modifier = Modifier.weight(1f)) {
                            when (layoutMode) {
                                KeyboardLayoutMode.TENGLISH, KeyboardLayoutMode.ENGLISH -> {
                                    TenglishKeyGrid(
                                        palette = palette,
                                        preferences = preferences,
                                        isShifted = isShifted,
                                        isEnglish = (layoutMode == KeyboardLayoutMode.ENGLISH),
                                        onKeyPress = { char ->
                                            triggerKeyEffect()
                                            if (layoutMode == KeyboardLayoutMode.TENGLISH) {
                                                activeComposingToken += char
                                            } else {
                                                commitTextInternal(char)
                                            }
                                            if (isShifted) isShifted = false
                                        },
                                        onShiftToggle = { isShifted = !isShifted },
                                        onBackspace = {
                                            triggerKeyEffect()
                                            if (activeComposingToken.isNotEmpty()) {
                                                activeComposingToken = activeComposingToken.dropLast(1)
                                            } else {
                                                deleteBackInternal()
                                            }
                                        },
                                        onBackspaceSwipeDelete = {
                                            triggerKeyEffect()
                                            activeComposingToken = ""
                                            deleteWordInternal()
                                        },
                                        onSpacePress = {
                                            triggerKeyEffect()
                                            if (activeComposingToken.isNotEmpty()) {
                                                val wordToCommit = teluguSug.ifEmpty { activeComposingToken }
                                                commitTextInternal(wordToCommit + " ")
                                                onLearnUserWord?.invoke(wordToCommit, activeComposingToken)
                                                previousCommittedWord = wordToCommit
                                                activeComposingToken = ""
                                            } else {
                                                commitTextInternal(" ")
                                            }
                                        },
                                        onSpaceLongPress = {
                                            showChangeKeyboardDialog = true
                                        },
                                        onSpaceCursorDrag = { dragDelta ->
                                            if (dragDelta > 15f) moveCursorInternal(1)
                                            else if (dragDelta < -15f) moveCursorInternal(-1)
                                        },
                                        onEnterPress = {
                                            triggerKeyEffect()
                                            if (activeComposingToken.isNotEmpty()) {
                                                commitTextInternal(teluguSug.ifEmpty { activeComposingToken })
                                                activeComposingToken = ""
                                            }
                                            commitTextInternal("\n")
                                        },
                                        onSwitchToSymbols = { layoutMode = KeyboardLayoutMode.SYMBOLS },
                                        onSwitchLayout = {
                                            layoutMode = if (layoutMode == KeyboardLayoutMode.TENGLISH) {
                                                KeyboardLayoutMode.NATIVE_TELUGU
                                            } else {
                                                KeyboardLayoutMode.TENGLISH
                                            }
                                        }
                                    )
                                }

                                KeyboardLayoutMode.NATIVE_TELUGU -> {
                                    NativeTeluguKeyGrid(
                                        palette = palette,
                                        preferences = preferences,
                                        isShifted = isShifted,
                                        onKeyPress = { char ->
                                            triggerKeyEffect()
                                            commitTextInternal(char)
                                            if (isShifted) isShifted = false
                                        },
                                        onReplaceLastCharAndCommit = { newChar ->
                                            triggerKeyEffect()
                                            deleteBackInternal()
                                            commitTextInternal(newChar)
                                        },
                                        onOpenGuninthaluPopup = { consonant ->
                                            triggerKeyEffect()
                                            selectedConsonantForPopup = consonant
                                        },
                                        onShiftToggle = { isShifted = !isShifted },
                                        onBackspace = {
                                            triggerKeyEffect()
                                            deleteBackInternal()
                                        },
                                        onBackspaceSwipeDelete = {
                                            triggerKeyEffect()
                                            deleteWordInternal()
                                        },
                                        onSpacePress = {
                                            triggerKeyEffect()
                                            commitTextInternal(" ")
                                        },
                                        onSpaceLongPress = {
                                            showChangeKeyboardDialog = true
                                        },
                                        onSpaceCursorDrag = { dragDelta ->
                                            if (dragDelta > 15f) moveCursorInternal(1)
                                            else if (dragDelta < -15f) moveCursorInternal(-1)
                                        },
                                        onEnterPress = {
                                            triggerKeyEffect()
                                            commitTextInternal("\n")
                                        },
                                        onSwitchToSymbols = { layoutMode = KeyboardLayoutMode.SYMBOLS },
                                        onSwitchLayout = { layoutMode = KeyboardLayoutMode.TENGLISH },
                                        onDismissKeyboard = onDismissKeyboard
                                    )
                                }

                                KeyboardLayoutMode.HANDWRITING -> {
                                    HandwritingPadSheet(
                                        palette = palette,
                                        preferences = preferences,
                                        onInsertChar = { char ->
                                            triggerKeyEffect()
                                            commitTextInternal(char)
                                        },
                                        onBackspace = {
                                            triggerKeyEffect()
                                            deleteBackInternal()
                                        },
                                        onSpace = {
                                            triggerKeyEffect()
                                            commitTextInternal(" ")
                                        },
                                        onEnter = {
                                            triggerKeyEffect()
                                            commitTextInternal("\n")
                                        },
                                        onSwitchToKeyboard = { layoutMode = KeyboardLayoutMode.TENGLISH }
                                    )
                                }

                                KeyboardLayoutMode.SYMBOLS, KeyboardLayoutMode.MORE_SYMBOLS -> {
                                    SymbolsKeyGrid(
                                        palette = palette,
                                        preferences = preferences,
                                        isMoreSymbols = isMoreSymbols,
                                        onKeyPress = { char ->
                                            triggerKeyEffect()
                                            commitTextInternal(char)
                                        },
                                        onToggleMoreSymbols = { isMoreSymbols = !isMoreSymbols },
                                        onBackspace = {
                                            triggerKeyEffect()
                                            deleteBackInternal()
                                        },
                                        onBackspaceSwipeDelete = {
                                            triggerKeyEffect()
                                            deleteWordInternal()
                                        },
                                        onSpacePress = {
                                            triggerKeyEffect()
                                            commitTextInternal(" ")
                                        },
                                        onSpaceLongPress = {
                                            showChangeKeyboardDialog = true
                                        },
                                        onSpaceCursorDrag = { dragDelta ->
                                            if (dragDelta > 15f) moveCursorInternal(1)
                                            else if (dragDelta < -15f) moveCursorInternal(-1)
                                        },
                                        onEnterPress = {
                                            triggerKeyEffect()
                                            commitTextInternal("\n")
                                        },
                                        onSwitchToLetters = { layoutMode = KeyboardLayoutMode.TENGLISH }
                                    )
                                }
                            }
                        }

                        if (oneHandedMode == OneHandedMode.LEFT) {
                            OneHandedSideBar(
                                palette = palette,
                                onExpand = { oneHandedMode = OneHandedMode.OFF },
                                onSwitchSide = { oneHandedMode = OneHandedMode.RIGHT },
                                modifier = Modifier.width(48.dp)
                            )
                        }
                    }
                }
            }

            // Realtime visual key tap animation overlay
            KeyEffectLayer(
                effectType = preferences.activeEffect,
                triggerTime = lastKeyTapTime,
                modifier = Modifier.matchParentSize()
            )

            // Dynamic Guninthalu Floating Popover
            selectedConsonantForPopup?.let { consonant ->
                GuninthaluPopup(
                    initialConsonant = consonant,
                    palette = palette,
                    onSelectChar = { char ->
                        commitTextInternal(char)
                        selectedConsonantForPopup = null
                    },
                    onDismiss = { selectedConsonantForPopup = null }
                )
            }

            // Quick Change Keyboard Selector Dialog (Long press spacebar feature)
            if (showChangeKeyboardDialog) {
                ChangeKeyboardDialog(
                    currentMode = layoutMode,
                    palette = palette,
                    onSelectMode = { newMode ->
                        layoutMode = newMode
                        activeComposingToken = ""
                        showChangeKeyboardDialog = false
                    },
                    onDismiss = {
                        showChangeKeyboardDialog = false
                    }
                )
            }
        }
        }
    }
}

@Composable
private fun VoiceTypingBanner(
    isListening: Boolean,
    palette: KeyboardPalette,
    onStop: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(12.dp)
    ) {
        androidx.compose.material3.Text(
            text = if (isListening) "🎙️ Listening in Telugu (మాట్లాడండి)..." else "Voice typing ready. Tap mic again.",
            fontSize = 14.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = palette.accent
        )
    }
}

@Composable
private fun OneHandedSideBar(
    palette: KeyboardPalette,
    onExpand: () -> Unit,
    onSwitchSide: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(palette.specialKeyBackground.copy(alpha = 0.5f))
            .padding(vertical = 12.dp, horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        IconButton(
            onClick = onExpand,
            modifier = Modifier
                .size(40.dp)
                .background(palette.keyBackground, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Fullscreen,
                contentDescription = "Expand Full Width",
                tint = palette.keyText,
                modifier = Modifier.size(22.dp)
            )
        }

        IconButton(
            onClick = onSwitchSide,
            modifier = Modifier
                .size(40.dp)
                .background(palette.keyBackground, CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.CompareArrows,
                contentDescription = "Switch Side",
                tint = palette.accent,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
