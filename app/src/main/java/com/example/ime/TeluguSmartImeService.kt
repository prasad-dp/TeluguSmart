package com.example.ime

import android.content.ClipboardManager
import android.content.Context
import android.inputmethodservice.InputMethodService
import android.text.InputType
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.View
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.TeluguSmartApplication
import com.example.ui.keyboard.KeyboardRootView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class TeluguSmartImeService : InputMethodService(), LifecycleOwner, SavedStateRegistryOwner, ViewModelStoreOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private val mViewModelStore = ViewModelStore()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val inputConnectionState = mutableStateOf<InputConnection?>(null)
    private val editorInfoState = mutableStateOf<EditorInfo?>(null)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry
    override val viewModelStore: ViewModelStore get() = mViewModelStore

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        // Clipboard listener protected against Android 10+ SecurityException
        try {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            clipboard?.addPrimaryClipChangedListener {
                try {
                    val clip = clipboard.primaryClip
                    if (clip != null && clip.itemCount > 0) {
                        val text = clip.getItemAt(0).text?.toString()
                        if (!text.isNullOrBlank()) {
                            val app = application as? TeluguSmartApplication
                            app?.repository?.let { repo ->
                                serviceScope.launch {
                                    repo.addClipboardText(text)
                                }
                            }
                        }
                    }
                } catch (_: Throwable) {
                    // Ignore clipboard read errors when not in focus
                }
            }
        } catch (_: Throwable) {}
    }

    override fun onEvaluateInputViewShown(): Boolean {
        super.onEvaluateInputViewShown()
        return true
    }

    override fun onShowInputRequested(flags: Int, configChange: Boolean): Boolean {
        return true
    }

    override fun onEvaluateFullscreenMode(): Boolean {
        return false
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        inputConnectionState.value = currentInputConnection
        editorInfoState.value = attribute
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        inputConnectionState.value = currentInputConnection
        editorInfoState.value = info
        if (!lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        }
        if (!lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }
    }

    override fun onWindowShown() {
        super.onWindowShown()
        inputConnectionState.value = currentInputConnection
        editorInfoState.value = currentInputEditorInfo
        if (!lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        }
        if (!lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }
    }

    override fun onFinishInput() {
        super.onFinishInput()
        inputConnectionState.value = null
        editorInfoState.value = null
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        if (lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        }
        if (lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        }
    }

    override fun onWindowHidden() {
        super.onWindowHidden()
        if (lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        }
        if (lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        }
    }

    override fun onCreateInputView(): View {
        if (!lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        }
        if (!lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }

        inputConnectionState.value = currentInputConnection
        editorInfoState.value = currentInputEditorInfo

        val composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@TeluguSmartImeService)
            setViewTreeSavedStateRegistryOwner(this@TeluguSmartImeService)
            setViewTreeViewModelStoreOwner(this@TeluguSmartImeService)
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnLifecycleDestroyed(this@TeluguSmartImeService))

            setContent {
                val app = application as? TeluguSmartApplication ?: return@setContent
                val prefs by app.repository.preferences.collectAsState()
                val snippets by app.repository.clipboardSnippets.collectAsState(initial = emptyList())
                val activeConnection by inputConnectionState
                val activeEditorInfo by editorInfoState

                KeyboardRootView(
                    preferences = prefs,
                    inputConnection = activeConnection,
                    editorInfo = activeEditorInfo,
                    onDismissKeyboard = {
                        requestHideSelf(0)
                    },
                    clipboardSnippets = snippets,
                    onSaveClipboardText = { text ->
                        serviceScope.launch {
                            app.repository.addClipboardText(text)
                        }
                    },
                    onTogglePinClipboard = { snippet ->
                        serviceScope.launch {
                            app.repository.togglePinClipboard(snippet)
                        }
                    },
                    onDeleteClipboard = { snippet ->
                        serviceScope.launch {
                            app.repository.deleteClipboardSnippet(snippet)
                        }
                    },
                    onClearUnpinnedClipboard = {
                        serviceScope.launch {
                            app.repository.clearUnpinnedClipboard()
                        }
                    },
                    onLearnUserWord = { telugu, phonetic ->
                        try {
                            val attribute = activeEditorInfo ?: currentInputEditorInfo
                            val isNoLearning = attribute != null && (attribute.imeOptions and EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING != 0)
                            val inputType = attribute?.inputType ?: 0
                            val isPassword = (inputType and InputType.TYPE_MASK_VARIATION) == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                                             (inputType and InputType.TYPE_MASK_VARIATION) == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD ||
                                             (inputType and InputType.TYPE_MASK_VARIATION) == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
                                             (inputType and InputType.TYPE_MASK_CLASS) == InputType.TYPE_CLASS_NUMBER && (inputType and InputType.TYPE_NUMBER_VARIATION_PASSWORD) != 0

                            if (!isNoLearning && !isPassword) {
                                serviceScope.launch {
                                    app.repository.insertOrUpdateWord(telugu, phonetic)
                                }
                            }
                        } catch (_: Throwable) {}
                    }
                )
            }
        }
        return composeView
    }

    override fun onDestroy() {
        super.onDestroy()
        if (lifecycleRegistry.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        }
        mViewModelStore.clear()
        serviceScope.cancel()
    }
}
