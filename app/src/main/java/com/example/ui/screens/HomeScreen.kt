package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.TeluguSmartApplication
import com.example.data.KeyboardPreferences
import com.example.engine.TeluguTransliterationEngine
import com.example.ui.components.EnglishMockup
import com.example.ui.components.HandwritingMockup
import com.example.ui.components.TranslitMockup
import com.example.ui.components.VarnamalaMockup
import com.example.ui.components.VoiceMockup
import com.example.ui.keyboard.KeyboardRootView
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandPrimaryDark
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.LightBorder
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class LayoutOption(
    val id: String,
    val title: String,
    val subtitle: String,
    val badge: String,
    val previewType: String,
    val sampleKeys: List<String>
)

@Composable
fun HomeScreen(
    preferences: KeyboardPreferences,
    onUpdatePreferences: (KeyboardPreferences) -> Unit,
    isDarkMode: Boolean
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    val app = context.applicationContext as? TeluguSmartApplication
    val snippets by (app?.repository?.clipboardSnippets?.collectAsState(initial = emptyList())
        ?: remember { mutableStateOf(emptyList()) })

    var selectedLayoutId by remember { mutableStateOf("telugu_translit") }
    var sandboxInput by remember { mutableStateOf("") }
    var sandboxOutput by remember { mutableStateOf("") }
    var isKeyboardVisible by remember { mutableStateOf(false) }

    // IME STATUS TRACKING FOR ANIMATED ENABLE KEYBOARD BANNER
    var isImeEnabled by remember { mutableStateOf(false) }
    var isImeSelected by remember { mutableStateOf(false) }

    val checkImeStatus = {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        val enabledList = imm?.enabledInputMethodList ?: emptyList()
        val packageName = context.packageName
        isImeEnabled = enabledList.any { it.packageName == packageName }

        val currentIme = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.DEFAULT_INPUT_METHOD
        ) ?: ""
        isImeSelected = currentIme.contains(packageName)
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                checkImeStatus()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        checkImeStatus()
    }

    // ==========================================
    // STITCH SYSTEM HOME PAGE ANIMATIONS
    // ==========================================
    val infiniteTransition = rememberInfiniteTransition(label = "home_animations")

    val logoScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "home_logo_scale"
    )

    val logoFloatY by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "home_logo_float"
    )

    val haloAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "home_halo_alpha"
    )

    val enableBtnPulseScale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "home_enable_btn_scale"
    )

    val enableBtnGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "home_enable_btn_glow"
    )

    val tapIconBob by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 650, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "home_tap_icon_bob"
    )

    val shimmerProgress by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "home_shimmer"
    )

    val bgOrbitalRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 36000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "home_bg_rotation"
    )

    val quickSamplePhrases = listOf(
        "namaste" to "నమస్తే",
        "elavunnavu" to "ఎలా ఉన్నారు",
        "dhanyavadalu" to "ధన్యవాదాలు",
        "telugu" to "తెలుగు",
        "shubhodhayam" to "శుభోదయం",
        "bagunnanu" to "బాగున్నాను",
        "manchi roju" to "మంచి రోజు"
    )

    val layouts = listOf(
        LayoutOption(
            id = "telugu_translit",
            title = "abc → తెలుగు",
            subtitle = "Phonetic Smart Typing",
            badge = "Recommended",
            previewType = "translit",
            sampleKeys = listOf("అలాగే", "నమస్కారం", "ఎలా ఉన్నారు")
        ),
        LayoutOption(
            id = "english",
            title = "English",
            subtitle = "Standard QWERTY",
            badge = "Default",
            previewType = "english",
            sampleKeys = listOf("Hello", "Beautiful", "Smart")
        ),
        LayoutOption(
            id = "handwriting",
            title = "Handwriting",
            subtitle = "వ్రాత • Finger Drawing",
            badge = "Touch Draw",
            previewType = "draw",
            sampleKeys = listOf("ఆ", "తెలుగు", "కలం")
        ),
        LayoutOption(
            id = "varnamala",
            title = "వర్ణమాల",
            subtitle = "Direct Alphabet Matrix",
            badge = "Varnamala",
            previewType = "varnamala",
            sampleKeys = listOf("అ", "ఆ", "ఇ", "ఈ", "క", "ఖ")
        ),
        LayoutOption(
            id = "voice",
            title = "Voice typing",
            subtitle = "వాయిస్ టైపింగ్ • Speech to Text",
            badge = "Audio",
            previewType = "voice",
            sampleKeys = listOf("మైక్ నొక్కి మాట్లాడండి")
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // ANIMATED BACKGROUND CANVAS WITH GEOMETRIC ORBITS
        val baseArcColor = if (isDarkMode) Color(0xFF38BDF8) else Color(0xFF0D9488)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        baseArcColor.copy(alpha = if (isDarkMode) 0.08f else 0.05f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.5f, h * 0.25f),
                    radius = w * 0.9f
                )
            )

            rotate(degrees = bgOrbitalRotation, pivot = Offset(w * 0.5f, h * 0.25f)) {
                drawCircle(
                    color = baseArcColor.copy(alpha = 0.08f),
                    radius = w * 0.6f,
                    center = Offset(w * 0.5f, h * 0.25f),
                    style = Stroke(width = 1f)
                )
            }
        }

        // MAIN SCROLLABLE CONTENT: 2-COLUMN LAYOUT CARDS + BOTTOM TYPING SANDBOX
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(
                start = 14.dp,
                end = 14.dp,
                top = 12.dp,
                bottom = if (isKeyboardVisible) 380.dp else 160.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // HERO BRAND LOGO BANNER WITH BREATHING FLOAT & RADIANT HALO
            item(span = { GridItemSpan(2) }) {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = if (isDarkMode) DarkBorder else LightBorder
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_hero_logo_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 18.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Animated Breathing Logo Container
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .offset { IntOffset(0, logoFloatY.roundToInt()) },
                            contentAlignment = Alignment.Center
                        ) {
                            // Radiant Halo Aura Behind Emblem
                            Box(
                                modifier = Modifier
                                    .size(105.dp)
                                    .scale(logoScale * 1.15f)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                (if (isDarkMode) Color(0xFF10B981) else BrandPrimary).copy(alpha = 0.35f * haloAlpha),
                                                (if (isDarkMode) Color(0xFF0D9488) else BrandSecondary).copy(alpha = 0.15f * haloAlpha),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )

                            // Official Logo Emblem
                            com.example.ui.components.TeluguSmartLogo(
                                size = 88.dp,
                                showText = false,
                                modifier = Modifier
                                    .scale(logoScale)
                                    .testTag("home_app_logo")
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "TeluguSmart Keyboard",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "తెలుగు టైపింగ్ • Ultra-Fast • 100% On-Device",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 3.dp)
                        )
                    }
                }
            }

            // PROMINENT ANIMATED ENABLE KEYBOARD STATUS BANNER (If not active)
            if (!isImeEnabled || !isImeSelected) {
                item(span = { GridItemSpan(2) }) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            Brush.horizontalGradient(
                                colors = listOf(
                                    if (isDarkMode) Color(0xFF10B981) else BrandPrimary,
                                    if (isDarkMode) Color(0xFF38BDF8) else BrandSecondary
                                )
                            )
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_enable_keyboard_banner")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(if (isImeEnabled) Color(0xFF10B981) else Color(0xFFEF4444))
                                    )
                                    Text(
                                        text = if (!isImeEnabled) "కీబోర్డ్ ఆఫ్‌లో ఉంది (Keyboard Disabled)"
                                        else "డిఫాల్ట్ కీబోర్డ్‌గా సెట్ చేయండి (Set as Default)",
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // ANIMATED ENABLE KEYBOARD CTA BUTTON WITH SHIMMER & PULSE
                            Box(contentAlignment = Alignment.Center) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .scale(enableBtnPulseScale * 1.06f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            Brush.radialGradient(
                                                colors = listOf(
                                                    (if (isDarkMode) Color(0xFF10B981) else BrandPrimary).copy(alpha = enableBtnGlowAlpha * 0.45f),
                                                    (if (isDarkMode) Color(0xFF38BDF8) else BrandSecondary).copy(alpha = enableBtnGlowAlpha * 0.20f),
                                                    Color.Transparent
                                                )
                                            )
                                        )
                                )

                                Button(
                                    onClick = {
                                        if (!isImeEnabled) {
                                            val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            context.startActivity(intent)
                                        } else {
                                            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                                            imm?.showInputMethodPicker()
                                        }
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isDarkMode) BrandPrimaryDark else BrandPrimary
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .scale(enableBtnPulseScale)
                                        .shadow(8.dp, RoundedCornerShape(14.dp))
                                        .testTag("home_enable_keyboard_btn")
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Canvas(modifier = Modifier.fillMaxSize()) {
                                            val w = size.width
                                            val sweepOffset = w * shimmerProgress
                                            drawRect(
                                                brush = Brush.horizontalGradient(
                                                    colors = listOf(
                                                        Color.Transparent,
                                                        Color.White.copy(alpha = 0.35f),
                                                        Color.Transparent
                                                    ),
                                                    startX = sweepOffset - 80f,
                                                    endX = sweepOffset + 80f
                                                )
                                            )
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.TouchApp,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.offset { IntOffset(0, tapIconBob.roundToInt()) }
                                            )
                                            Text(
                                                text = if (!isImeEnabled) "కీబోర్డ్ ఆన్ చేయండి (Enable Keyboard)"
                                                else "డిఫాల్ట్ గా ఎంచుకోండి (Select Keyboard)",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Header Title Section
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Keyboard Layouts",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "మీకు అనుకూలమైన లేఅవుట్‌ను ఎంచుకోండి",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = BrandPrimary.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "${layouts.size} Layouts",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // 2-Column Grid of Layout Options (Previous visual version restored)
            items(layouts) { layout ->
                val isSelected = selectedLayoutId == layout.id
                LayoutPreviewCard(
                    layout = layout,
                    isSelected = isSelected,
                    isDarkMode = isDarkMode,
                    onClick = {
                        selectedLayoutId = layout.id
                        Toast.makeText(context, "Layout: ${layout.title}", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // Bottom Typing Sandbox Card
            item(span = { GridItemSpan(2) }) {
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (isDarkMode) Color(0xFF10B981).copy(alpha = 0.4f) else Color(0xFF00684A).copy(alpha = 0.35f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("typing_sandbox_card")
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(BrandPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TextFields,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                                Text(
                                    text = "Typing Sandbox (టైపింగ్ ప్రాక్టీస్)",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (sandboxInput.isNotEmpty()) {
                                    IconButton(
                                        onClick = {
                                            sandboxInput = ""
                                            sandboxOutput = ""
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = BrandPrimary,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { isKeyboardVisible = !isKeyboardVisible }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Keyboard,
                                            contentDescription = "Toggle Keyboard",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = if (isKeyboardVisible) "Hide" else "Test Keyboard",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Input Field
                        OutlinedTextField(
                            value = sandboxInput,
                            onValueChange = { input ->
                                sandboxInput = input
                                sandboxOutput = TeluguTransliterationEngine.convertPhonetic(input)
                            },
                            placeholder = {
                                Text("ఇక్కడ టైప్ చేయండి (Type here to test)...", fontSize = 12.sp)
                            },
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester)
                                .testTag("home_sandbox_textfield"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandPrimary,
                                unfocusedBorderColor = if (isDarkMode) DarkBorder else LightBorder
                            )
                        )

                        // Real-time Converted Telugu Output Display
                        if (sandboxOutput.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BrandPrimary.copy(alpha = if (isDarkMode) 0.2f else 0.12f),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    BrandPrimary.copy(alpha = 0.4f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "తెలుగు ఔట్‌పుట్ (Telugu Output):",
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isDarkMode) Color(0xFF34D399) else Color(0xFF047857)
                                        )
                                        Text(
                                            text = sandboxOutput,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDarkMode) Color.White else Color(0xFF064E3B)
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                            val clip = ClipData.newPlainText("Telugu Text", sandboxOutput)
                                            clipboard?.setPrimaryClip(clip)
                                            Toast.makeText(context, "Copied: $sandboxOutput", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy Text",
                                            tint = if (isDarkMode) Color(0xFF34D399) else BrandPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick Try Sample Pills
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(quickSamplePhrases) { (en, te) ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isDarkMode) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isDarkMode) DarkBorder else LightBorder
                                    ),
                                    modifier = Modifier.clickable {
                                        sandboxInput = en
                                        sandboxOutput = te
                                        isKeyboardVisible = true
                                    }
                                ) {
                                    Text(
                                        text = "$en → $te",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // POP-UP KEYBOARD FROM BOTTOM WHEN INPUT IS TAPPED
        AnimatedVisibility(
            visible = isKeyboardVisible,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                    .testTag("popup_keyboard_container"),
                shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Header Bar of Popup Keyboard
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isDarkMode) Color(0xFF1E293B) else Color(0xFFE2E8F0))
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            )
                            Text(
                                text = "TeluguSmart Keyboard",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkMode) Color(0xFF34D399) else Color(0xFF047857)
                            )
                        }

                        Text(
                            text = "Close ✕",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandPrimary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { isKeyboardVisible = false }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Floating preview bar on top of keyboard so input is NEVER obscured!
                    if (sandboxInput.isNotEmpty() || sandboxOutput.isNotEmpty()) {
                        Surface(
                            color = BrandPrimary.copy(alpha = if (isDarkMode) 0.2f else 0.12f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Input: $sandboxInput",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = sandboxOutput.ifEmpty { sandboxInput },
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDarkMode) Color(0xFF34D399) else Color(0xFF047857)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        sandboxInput = ""
                                        sandboxOutput = ""
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Interactive Keyboard Root View
                    KeyboardRootView(
                        preferences = preferences,
                        currentBufferText = sandboxInput,
                        onBufferTextChange = { newText ->
                            sandboxInput = newText
                            sandboxOutput = TeluguTransliterationEngine.convertPhonetic(newText)
                        },
                        clipboardSnippets = snippets,
                        onSaveClipboardText = { text ->
                            app?.repository?.let { repo ->
                                coroutineScope.launch {
                                    repo.addClipboardText(text)
                                }
                            }
                        },
                        onTogglePinClipboard = { snippet ->
                            app?.repository?.let { repo ->
                                coroutineScope.launch {
                                    repo.togglePinClipboard(snippet)
                                }
                            }
                        },
                        onDeleteClipboard = { snippet ->
                            app?.repository?.let { repo ->
                                coroutineScope.launch {
                                    repo.deleteClipboardSnippet(snippet)
                                }
                            }
                        },
                        onClearUnpinnedClipboard = {
                            app?.repository?.let { repo ->
                                coroutineScope.launch {
                                    repo.clearUnpinnedClipboard()
                                }
                            }
                        },
                        onLearnUserWord = { telugu, phonetic ->
                            app?.repository?.let { repo ->
                                coroutineScope.launch {
                                    repo.insertOrUpdateWord(telugu, phonetic)
                                }
                            }
                        },
                        onDismissKeyboard = {
                            isKeyboardVisible = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LayoutPreviewCard(
    layout: LayoutOption,
    isSelected: Boolean,
    isDarkMode: Boolean,
    onClick: () -> Unit
) {
    val cardScale by animateFloatAsState(
        targetValue = if (isSelected) 1.03f else 1.0f,
        animationSpec = tween(durationMillis = 250, easing = EaseInOutCubic),
        label = "card_selection_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .scale(cardScale)
            .clickable(onClick = onClick)
            .testTag("layout_card_${layout.id}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Keyboard Preview Box matching previous version
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(115.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = if (isSelected) Color(0xFF00684A) else if (isDarkMode) Color(0xFF334155) else Color(0xFFCBD5E1),
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            when (layout.id) {
                "telugu_translit" -> TranslitMockup(modifier = Modifier.fillMaxSize())
                "english" -> EnglishMockup(modifier = Modifier.fillMaxSize())
                "handwriting" -> HandwritingMockup(modifier = Modifier.fillMaxSize())
                "varnamala" -> VarnamalaMockup(modifier = Modifier.fillMaxSize())
                "voice" -> VoiceMockup(modifier = Modifier.fillMaxSize())
                else -> TranslitMockup(modifier = Modifier.fillMaxSize())
            }

            // Green Diagonal Corner Ribbon with Checkmark
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val path = Path().apply {
                            moveTo(size.width, 0f)
                            lineTo(size.width, size.height)
                            lineTo(0f, size.height)
                            close()
                        }
                        drawPath(path, color = Color(0xFF00684A))
                    }
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 2.dp, bottom = 2.dp)
                            .size(15.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Bottom Pill Button
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            color = if (isSelected) Color(0xFF00684A) else if (isDarkMode) Color(0xFF1E293B) else Color(0xFFFFFFFF),
            border = if (!isSelected) androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isDarkMode) Color(0xFF334155) else Color(0xFFCBD5E1)
            ) else null,
            shadowElevation = if (isSelected) 2.dp else 0.dp
        ) {
            Text(
                text = layout.title,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = if (isSelected) Color.White else if (isDarkMode) Color(0xFFF1F5F9) else Color(0xFF0F172A),
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }
    }
}
