package com.example.ui.keyboard

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material.icons.automirrored.rounded.KeyboardReturn
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.KeyboardCapslock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.KeyboardPreferences

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
    palette: KeyboardPalette,
    preferences: KeyboardPreferences,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    val view = LocalView.current
    val shape = RoundedCornerShape(preferences.keyCornerRadiusDp.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Tactile keypress scale spring
    val keyScale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "keyScale"
    )

    val bg = when {
        isAccent -> palette.accent
        isSpecial -> palette.specialKeyBackground
        else -> palette.keyBackground
    }

    val textCol = when {
        isAccent -> palette.accentText
        isSpecial -> palette.specialKeyText
        else -> palette.keyText
    }

    Box(
        modifier = modifier
            .padding(horizontal = 2.5.dp, vertical = 3.dp)
            .height(44.dp)
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
                    if (preferences.keyHapticFeedback) {
                        try {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        } catch (_: Exception) {}
                    }
                    onClick()
                },
                onLongClick = onLongClick?.let {
                    {
                        if (preferences.keyHapticFeedback) {
                            try {
                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                            } catch (_: Exception) {}
                        }
                        it()
                    }
                }
            )
            .testTag(if (primaryText.isNotEmpty()) "key_$primaryText" else "key_special"),
        contentAlignment = Alignment.Center
    ) {
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
    }
}
