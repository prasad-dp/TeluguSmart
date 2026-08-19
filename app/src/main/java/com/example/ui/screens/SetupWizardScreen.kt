package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.engine.VoiceInstructionHelper
import com.example.ui.components.TeluguSmartLogo

/**
 * Setup and Onboarding Wizard matching exact UX from Screenshots 1-4:
 * - Serene light curved background
 * - Centered Logo & Trust verification badge
 * - Step 1: Voice Guidance + "Activate Keyboard" button
 * - Step 2: Voice Guidance + "Select Keyboard" button
 */
@Composable
fun SetupWizardScreen(
    isDarkMode: Boolean = false,
    onToggleDarkMode: () -> Unit = {},
    onComplete: () -> Unit = {}
) {
    val context = LocalContext.current
    var isImeEnabled by remember { mutableStateOf(false) }
    var isImeSelected by remember { mutableStateOf(false) }

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

    // Determine current Step: Step 1 (Enable) or Step 2 (Select)
    val currentStep = if (!isImeEnabled) 1 else if (!isImeSelected) 2 else 3

    val speakStepInstruction = {
        if (currentStep == 1) {
            voiceHelper.speak(
                textTelugu = "తెలుగు స్మార్ట్ కీబోర్డ్ ఆక్టివేట్ చేయడానికి క్రింద ఉన్న 'Activate Keyboard' బటన్ నొక్కండి, ఆపై లిస్ట్‌లో TeluguSmart Keyboard ను ఎనేబుల్ చేయండి.",
                textEnglishFallback = "To activate the keyboard, tap the Activate Keyboard button below, then toggle on TeluguSmart Keyboard."
            )
        } else if (currentStep == 2) {
            voiceHelper.speak(
                textTelugu = "ఇప్పుడు 'Select Keyboard' బటన్ నొక్కి లిస్ట్ నుండి TeluguSmart Keyboard ను మీ డిఫాల్ట్ కీబోర్డ్‌గా ఎంచుకోండి.",
                textEnglishFallback = "Now tap the Select Keyboard button and choose TeluguSmart Keyboard as your default keyboard."
            )
        } else {
            voiceHelper.speak(
                textTelugu = "అభినందనలు! తెలుగు స్మార్ట్ కీబోర్డ్ విజయవంతంగా సెట్ చేయబడింది.",
                textEnglishFallback = "Congratulations! TeluguSmart Keyboard is now active and ready to type."
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Subtle Serene Curved Arc Geometric Background (as in Screenshots 1-2)
        val arcLineColor = if (isDarkMode) Color(0xFF1E293B).copy(alpha = 0.6f) else Color(0xFFE2E8F0).copy(alpha = 0.8f)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            drawCircle(
                color = arcLineColor,
                radius = w * 0.7f,
                center = Offset(w * 0.5f, h * 0.15f),
                style = Stroke(width = 1.5f)
            )
            drawCircle(
                color = arcLineColor,
                radius = w * 0.95f,
                center = Offset(w * 0.5f, h * 0.55f),
                style = Stroke(width = 1.5f)
            )
            drawLine(
                color = arcLineColor,
                start = Offset(w * 0.2f, 0f),
                end = Offset(w * 0.2f, h),
                strokeWidth = 1.2f
            )
            drawLine(
                color = arcLineColor,
                start = Offset(0f, h * 0.42f),
                end = Offset(w, h * 0.42f),
                strokeWidth = 1.2f
            )
            drawLine(
                color = arcLineColor,
                start = Offset(0f, h * 0.62f),
                end = Offset(w, h * 0.62f),
                strokeWidth = 1.2f
            )
        }

        // Top Navigation Controls (Dark Mode Toggle & Overflow)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 36.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onToggleDarkMode,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
                    .testTag("setup_dark_mode_toggle")
            ) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Dark Mode",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = speakStepInstruction,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF059669).copy(alpha = 0.15f))
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Voice Guide",
                    tint = Color(0xFF059669),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Main Center Brand & Trust Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // Official App Logo
            TeluguSmartLogo(
                size = 110.dp,
                showText = false,
                modifier = Modifier.testTag("setup_app_logo")
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "TeluguSmart Keyboard",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "తెలుగు టైపింగ్ • Zero Latency • 100% Private",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Trust & Security Card (Matching Screenshot 1 & 2)
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Security",
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "Trusted by millions of users",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF059669)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "This keyboard does not collect any sensitive information. A standard Android warning is shown whenever any third-party keyboard is activated.",
                        fontSize = 12.5.sp,
                        lineHeight = 17.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Bottom Persistent Action Flow (Matching Screenshots 1 & 2)
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 22.dp),
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
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Step 1",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    IconButton(
                                        onClick = speakStepInstruction,
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .border(1.5.dp, Color(0xFF059669), CircleShape)
                                            .background(Color(0xFF059669).copy(alpha = 0.1f))
                                            .testTag("speaker_step_1")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeUp,
                                            contentDescription = "Voice Guidance Step 1",
                                            tint = Color(0xFF059669),
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                Button(
                                    onClick = {
                                        val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        context.startActivity(intent)
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF047857) // Deep emerald green
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp)
                                        .testTag("btn_activate_keyboard")
                                ) {
                                    Text(
                                        text = "Activate Keyboard",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        2 -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Step 2",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    IconButton(
                                        onClick = speakStepInstruction,
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .border(1.5.dp, Color(0xFF059669), CircleShape)
                                            .background(Color(0xFF059669).copy(alpha = 0.1f))
                                            .testTag("speaker_step_2")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeUp,
                                            contentDescription = "Voice Guidance Step 2",
                                            tint = Color(0xFF059669),
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                Button(
                                    onClick = {
                                        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                                        imm?.showInputMethodPicker()
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF047857)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp)
                                        .testTag("btn_select_keyboard")
                                ) {
                                    Text(
                                        text = "Select Keyboard",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        else -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Success",
                                        tint = Color(0xFF059669),
                                        modifier = Modifier.size(26.dp)
                                    )
                                    Text(
                                        text = "Keyboard is Active!",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF059669)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = onComplete,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF047857)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp)
                                        .testTag("btn_continue_dashboard")
                                ) {
                                    Text(
                                        text = "Open Keyboard Dashboard",
                                        fontSize = 16.sp,
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
}
