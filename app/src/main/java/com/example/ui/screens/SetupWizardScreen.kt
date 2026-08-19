package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
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
import com.example.engine.VoiceInstructionHelper
import com.example.ui.components.TeluguSmartLogo
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandPrimaryDark
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandSecondaryDark
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.LightBorder
import kotlin.math.roundToInt

/**
 * Animated Landing / Setup Screen (Stitch Design System):
 * - Ambient floating & radiant halo animations matching Light and Dark palettes
 * - Auto-plays voice instruction guidance on launch
 * - Top Audio Mute/Unmute button with pulsing live audio indicator
 * - Clean hero emblem, privacy trust card, and animated CTA button
 */
@Composable
fun SetupWizardScreen(
    isDarkMode: Boolean = false,
    onToggleDarkMode: () -> Unit = {},
    onComplete: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isImeEnabled by remember { mutableStateOf(false) }
    var isImeSelected by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(false) }

    val voiceHelper = remember { VoiceInstructionHelper(context) }

    DisposableEffect(Unit) {
        onDispose {
            voiceHelper.shutdown()
        }
    }

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

    val currentStep = if (!isImeEnabled) 1 else if (!isImeSelected) 2 else 3

    // Automatically trigger voice guidance upon launch / step progression unless muted
    LaunchedEffect(currentStep, isMuted) {
        if (isMuted) {
            voiceHelper.stop()
        } else {
            when (currentStep) {
                1 -> voiceHelper.speak(
                    textTelugu = "నమస్కారం! తెలుగు స్మార్ట్ కీబోర్డ్ ఆక్టివేట్ చేయడానికి క్రింద ఉన్న 'కీబోర్డ్ ఆన్ చేయండి' బటన్ నొక్కండి, ఆపై సెట్టింగ్స్‌లో TeluguSmart Keyboard ను ఎనేబుల్ చేయండి.",
                    textEnglishFallback = "Welcome! To activate, tap the Enable Keyboard button below and toggle on TeluguSmart Keyboard."
                )
                2 -> voiceHelper.speak(
                    textTelugu = "ఇప్పుడు 'డిఫాల్ట్ గా ఎంచుకోండి' బటన్ నొక్కి లిస్ట్ నుండి TeluguSmart Keyboard ను మీ ప్రాథమిక కీబోర్డ్‌గా ఎంచుకోండి.",
                    textEnglishFallback = "Now tap Select Default and choose TeluguSmart Keyboard as your default keyboard."
                )
                else -> voiceHelper.speak(
                    textTelugu = "అభినందనలు! తెలుగు స్మార్ట్ కీబోర్డ్ విజయవంతంగా సిద్ధమైంది. మీరు ఇప్పుడు తెలుగులో సులభంగా టైప్ చేయవచ్చు.",
                    textEnglishFallback = "Congratulations! TeluguSmart Keyboard is now active and ready for fast typing."
                )
            }
        }
    }

    // ==========================================
    // STITCH SYSTEM AMBIENT ANIMATIONS
    // ==========================================
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_animations")

    // 1. Logo Floating Float & Scale Breathing Animation
    val logoScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_scale"
    )

    val logoFloatY by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_float"
    )

    // 2. Halo Radiance Pulse behind the Logo
    val haloAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo_alpha"
    )

    // 3. Subtle Background Orbit Rotation & Arc Pulse
    val orbitalRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 32000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbital_rotation"
    )

    val bgArcAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bg_arc_alpha"
    )

    // 4. Audio Active Ripple Ring Animation (for Top Mute Button)
    val audioRippleScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Restart
        ),
        label = "audio_ripple_scale"
    )
    val audioRippleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Restart
        ),
        label = "audio_ripple_alpha"
    )

    // 5. Action Button Shimmer Offset
    val shimmerProgress by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "button_shimmer"
    )

    // 6. Enable Keyboard Button Pulse & Glowing Aura Animation
    val enableBtnPulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "enable_btn_scale"
    )
    val enableBtnGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "enable_btn_glow"
    )
    val tapIconBob by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 650, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tap_icon_bob"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Ambient Canvas Background with subtle animated celestial orbits & arcs
        val baseArcColor = if (isDarkMode) Color(0xFF38BDF8) else Color(0xFF0D9488)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Glowing radiant ambient center
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        baseArcColor.copy(alpha = if (isDarkMode) 0.12f * bgArcAlpha else 0.08f * bgArcAlpha),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.5f, h * 0.32f),
                    radius = w * 0.85f
                )
            )

            // Dynamic rotating geometric orbital rings
            rotate(degrees = orbitalRotation, pivot = Offset(w * 0.5f, h * 0.32f)) {
                drawCircle(
                    color = baseArcColor.copy(alpha = 0.15f * bgArcAlpha),
                    radius = w * 0.55f,
                    center = Offset(w * 0.5f, h * 0.32f),
                    style = Stroke(width = 1.2f)
                )
                drawCircle(
                    color = baseArcColor.copy(alpha = 0.10f * bgArcAlpha),
                    radius = w * 0.78f,
                    center = Offset(w * 0.5f, h * 0.32f),
                    style = Stroke(width = 1.0f)
                )
            }

            // Subtle curved horizon guidelines
            drawLine(
                color = (if (isDarkMode) DarkBorder else LightBorder).copy(alpha = 0.6f),
                start = Offset(0f, h * 0.42f),
                end = Offset(w, h * 0.42f),
                strokeWidth = 1f
            )
        }

        // Main Welcome Body with Animated Breathing Logo & Cards
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 90.dp, bottom = 120.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Animated Hero Container (Breathing Scale + Vertical Float + Radiant Halo)
            Box(
                modifier = Modifier
                    .size(170.dp)
                    .offset { IntOffset(0, logoFloatY.roundToInt()) },
                contentAlignment = Alignment.Center
            ) {
                // Radiant Halo Aura Behind Emblem
                Box(
                    modifier = Modifier
                        .size(150.dp)
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

                // Official 3D Keyboard Emblem
                TeluguSmartLogo(
                    size = 120.dp,
                    showText = false,
                    modifier = Modifier
                        .scale(logoScale)
                        .testTag("setup_app_logo")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Brand Titles
            Text(
                text = "TeluguSmart Keyboard",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Text(
                text = "తెలుగు టైపింగ్ • Ultra-Fast • 100% Private",
                fontSize = 13.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // 100% On-Device Privacy & Security Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = if (isDarkMode) DarkBorder else LightBorder
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Security",
                            tint = if (isDarkMode) BrandPrimaryDark else BrandPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "100% ఆన్-డివైస్ ప్రైవసీ (Private & Secure)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) BrandPrimaryDark else BrandPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "మీరు టైప్ చేసే సమాచారం లేదా పాస్‌వర్డ్‌లు ఏవీ సర్వర్‌కు పంపబడవు. TeluguSmart పూర్తిగా ఆఫ్‌లైన్‌లో మీ ఫోన్‌లో మాత్రమే పనిచేస్తుంది.",
                        fontSize = 12.sp,
                        lineHeight = 17.5.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Top Navigation Bar: Audio Mute Button (with live animated pulse) & Dark Mode Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Audio Mute/Unmute Button with Animated Audio Radiance
            Box(contentAlignment = Alignment.Center) {
                // Expanding ripple when audio is active (unmuted)
                if (!isMuted) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .scale(audioRippleScale)
                            .clip(CircleShape)
                            .background(
                                (if (isDarkMode) Color(0xFF10B981) else BrandPrimary).copy(alpha = audioRippleAlpha)
                            )
                    )
                }

                IconButton(
                    onClick = { isMuted = !isMuted },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            if (isMuted) MaterialTheme.colorScheme.surfaceVariant
                            else BrandPrimary.copy(alpha = if (isDarkMode) 0.28f else 0.16f)
                        )
                        .border(
                            width = 1.dp,
                            color = if (isMuted) (if (isDarkMode) DarkBorder else LightBorder)
                            else BrandPrimary.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                        .testTag("audio_mute_toggle_btn")
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = if (isMuted) "Unmute Audio" else "Mute Audio",
                        tint = if (isMuted) MaterialTheme.colorScheme.onSurfaceVariant else (if (isDarkMode) Color(0xFF10B981) else BrandPrimary),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Dark/Light Mode Switcher
            IconButton(
                onClick = onToggleDarkMode,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        width = 1.dp,
                        color = if (isDarkMode) DarkBorder else LightBorder,
                        shape = CircleShape
                    )
                    .testTag("setup_dark_mode_toggle")
            ) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Dark Mode",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Bottom Persistent Action Button with Animated Glowing Shimmer
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = if (isDarkMode) DarkBorder else LightBorder
            ),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "setup_step_transition"
                ) { step ->
                    when (step) {
                        1 -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    // Animated Radiant Halo Glow & Concentric Ripple Rings behind Enable Keyboard Button
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(58.dp)
                                            .scale(enableBtnPulseScale * 1.08f)
                                            .clip(RoundedCornerShape(18.dp))
                                            .background(
                                                Brush.radialGradient(
                                                    colors = listOf(
                                                        (if (isDarkMode) Color(0xFF10B981) else BrandPrimary).copy(alpha = enableBtnGlowAlpha * 0.5f),
                                                        (if (isDarkMode) Color(0xFF38BDF8) else BrandSecondary).copy(alpha = enableBtnGlowAlpha * 0.25f),
                                                        Color.Transparent
                                                    )
                                                )
                                            )
                                    )

                                    Button(
                                        onClick = {
                                            val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            context.startActivity(intent)
                                        },
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isDarkMode) BrandPrimaryDark else BrandPrimary
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp)
                                            .scale(enableBtnPulseScale)
                                            .shadow(10.dp, RoundedCornerShape(14.dp))
                                            .testTag("enable_keyboard_btn")
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            // Shimmer light sweep overlay
                                            Canvas(modifier = Modifier.fillMaxSize()) {
                                                val w = size.width
                                                val h = size.height
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
                                                    text = "కీబోర్డ్ ఆన్ చేయండి (Enable Keyboard)",
                                                    fontSize = 14.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        2 -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    // Animated Radiant Halo Glow & Concentric Ripple Rings behind Select Keyboard Button
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(58.dp)
                                            .scale(enableBtnPulseScale * 1.08f)
                                            .clip(RoundedCornerShape(18.dp))
                                            .background(
                                                Brush.radialGradient(
                                                    colors = listOf(
                                                        (if (isDarkMode) Color(0xFF10B981) else BrandPrimary).copy(alpha = enableBtnGlowAlpha * 0.5f),
                                                        (if (isDarkMode) Color(0xFF38BDF8) else BrandSecondary).copy(alpha = enableBtnGlowAlpha * 0.25f),
                                                        Color.Transparent
                                                    ),
                                                    radius = 300f
                                                )
                                            )
                                    )

                                    Button(
                                        onClick = {
                                            try {
                                                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                                                imm?.showInputMethodPicker()
                                            } catch (_: Exception) {}
                                            coroutineScope.launch {
                                                for (i in 1..20) {
                                                    delay(350)
                                                    checkImeStatus()
                                                    if (isImeSelected) {
                                                        onComplete()
                                                        break
                                                    }
                                                }
                                            }
                                        },
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isDarkMode) BrandPrimaryDark else BrandPrimary
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp)
                                            .scale(enableBtnPulseScale)
                                            .shadow(10.dp, RoundedCornerShape(14.dp))
                                            .testTag("select_keyboard_btn")
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            // Shimmer light sweep overlay
                                            Canvas(modifier = Modifier.fillMaxSize()) {
                                                val w = size.width
                                                val h = size.height
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
                                                    imageVector = Icons.Default.Keyboard,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.offset { IntOffset(0, tapIconBob.roundToInt()) }
                                                )
                                                Text(
                                                    text = "డిఫాల్ట్ గా ఎంచుకోండి (Select Keyboard)",
                                                    fontSize = 14.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        else -> {
                            Button(
                                onClick = onComplete,
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isDarkMode) BrandPrimaryDark else BrandPrimary
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("finish_setup_btn")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "టైపింగ్ ప్రారంభించండి (Start Typing)",
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
