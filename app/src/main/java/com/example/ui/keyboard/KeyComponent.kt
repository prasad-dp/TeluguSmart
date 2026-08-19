package com.example.ui.keyboard

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material.icons.automirrored.rounded.KeyboardReturn
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.KeyboardCapslock
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.ui.unit.Dp
import com.example.data.KeyboardPreferences
import com.example.engine.SoundFeedbackHelper

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KeyComponent(
    modifier: Modifier = Modifier,
    primaryText: String = "",
    secondaryText: String? = null,
    icon: (@Composable () -> Unit)? = null,
    iconVector: ImageVector? = null,
    isSpecial: Boolean = false,
    isAccent: Boolean = false,
    keyHeight: Dp = 42.dp,
    palette: KeyboardPalette,
    preferences: KeyboardPreferences,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val view = LocalView.current
    val shape = RoundedCornerShape(preferences.keyCornerRadiusDp.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    var lastTapTime by remember { mutableStateOf(0L) }

    LaunchedEffect(Unit) {
        SoundFeedbackHelper.initialize(context)
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            lastTapTime = System.currentTimeMillis()
        }
    }

    // Tactile keypress scale spring for realistic key depression
    val keyScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "keyScale"
    )

    val rawBg = when {
        isAccent -> palette.accent
        isSpecial -> palette.specialKeyBackground
        else -> palette.keyBackground
    }

    val bg = if (preferences.customPhotoUri != null || preferences.customPhotoPreset != null) {
        if (isAccent) rawBg else rawBg.copy(alpha = preferences.keyOpacity.coerceIn(0.2f, 1.0f))
    } else rawBg

    val textCol = when {
        isAccent -> palette.accentText
        isSpecial -> palette.specialKeyText
        else -> palette.keyText
    }

    // Character key popup preview bubble check
    val showPopup = preferences.showPopupOnKeyPress &&
            (isPressed || (System.currentTimeMillis() - lastTapTime < 140L)) &&
            primaryText.isNotEmpty() &&
            primaryText.length <= 3 &&
            !isSpecial &&
            primaryText != "␣" && primaryText != " Space " && primaryText != "⌫" && primaryText != "↵" && primaryText != "⇧" && primaryText != "⇪"

    Box(
        modifier = modifier
            .padding(horizontal = 2.dp, vertical = 2.dp)
            .height(keyHeight)
            .scale(keyScale)
            .shadow(if (preferences.keyBorderEnabled) 1.dp else 0.dp, shape)
            .clip(shape)
            .background(bg)
            .then(
                if (preferences.keyBorderEnabled) {
                    Modifier.border(0.5.dp, palette.border, shape)
                } else Modifier
            )
            .combinedClickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(color = palette.accent),
                onClick = {
                    lastTapTime = System.currentTimeMillis()
                    if (preferences.keyHapticFeedback) {
                        try {
                            SoundFeedbackHelper.triggerHaptic(preferences.hapticStrength)
                        } catch (_: Exception) {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        }
                    }
                    if (preferences.keySoundFeedback) {
                        try {
                            SoundFeedbackHelper.playKeySound(preferences.soundProfile)
                        } catch (_: Exception) {}
                    }
                    onClick()
                },
                onLongClick = onLongClick?.let {
                    {
                        lastTapTime = System.currentTimeMillis()
                        if (preferences.keyHapticFeedback) {
                            try {
                                SoundFeedbackHelper.triggerHaptic(preferences.hapticStrength)
                            } catch (_: Exception) {
                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                            }
                        }
                        if (preferences.keySoundFeedback) {
                            try {
                                SoundFeedbackHelper.playKeySound(preferences.soundProfile)
                            } catch (_: Exception) {}
                        }
                        it()
                    }
                }
            )
            .testTag(if (primaryText.isNotEmpty()) "key_$primaryText" else "key_special"),
        contentAlignment = Alignment.Center
    ) {
        // Real-time Key Tap Effect Layer
        KeyEffectLayer(
            effectType = preferences.activeEffect,
            triggerTime = lastTapTime,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (secondaryText != null && preferences.showNumberRow) {
                Text(
                    text = secondaryText,
                    color = palette.keySecondaryText,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 10.sp,
                    modifier = Modifier.padding(top = 1.dp)
                )
            }

            when {
                icon != null -> {
                    icon()
                }
                iconVector != null -> {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = primaryText.ifEmpty { "Key Icon" },
                        tint = textCol,
                        modifier = Modifier.size(24.dp)
                    )
                }
                primaryText == "↵" || primaryText.equals("enter", ignoreCase = true) -> {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardReturn,
                        contentDescription = "Enter",
                        tint = textCol,
                        modifier = Modifier.size(24.dp)
                    )
                }
                primaryText == "⇪" -> {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardCapslock,
                        contentDescription = "Caps Lock",
                        tint = textCol,
                        modifier = Modifier.size(23.dp)
                    )
                }
                primaryText == "⇧" -> {
                    Icon(
                        imageVector = Icons.Rounded.ArrowUpward,
                        contentDescription = "Shift",
                        tint = textCol,
                        modifier = Modifier.size(22.dp)
                    )
                }
                primaryText == "⌫" -> {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Backspace,
                        contentDescription = "Backspace",
                        tint = textCol,
                        modifier = Modifier.size(22.dp)
                    )
                }
                else -> {
                    Text(
                        text = primaryText,
                        color = textCol,
                        fontSize = if (primaryText.length > 2) 13.sp else 17.sp,
                        fontWeight = if (isSpecial || isAccent) FontWeight.Bold else FontWeight.Normal,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Magnified Key Press Character Popup Bubble (desh keyboard style)
        if (showPopup) {
            KeyPopupBubble(
                text = primaryText,
                palette = palette,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-52).dp)
                    .zIndex(100f)
            )
        }
    }
}

@Composable
private fun KeyPopupBubble(
    text: String,
    palette: KeyboardPalette,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .size(width = 48.dp, height = 52.dp)
            .shadow(8.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        color = palette.keyBackground,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, palette.accent)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = palette.keyText,
                fontSize = if (text.length > 2) 15.sp else 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
