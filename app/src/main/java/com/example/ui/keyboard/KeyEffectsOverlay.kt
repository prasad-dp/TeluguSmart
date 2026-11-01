package com.example.ui.keyboard

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.KeyboardEffectType
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class Particle(
    val id: Long,
    val startX: Float,
    val startY: Float,
    val angle: Double,
    val speed: Float,
    val color: Color,
    val size: Float,
    val shape: String = "circle"
)

@Composable
fun KeyEffectLayer(
    effectType: KeyboardEffectType,
    triggerTime: Long,
    modifier: Modifier = Modifier
) {
    if (effectType == KeyboardEffectType.NONE || triggerTime == 0L) return

    val progress = remember(triggerTime) { Animatable(0f) }

    LaunchedEffect(triggerTime) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 380, easing = LinearOutSlowInEasing)
        )
    }

    val currentProgress = progress.value
    if (currentProgress >= 1f) return

    val particles = remember(triggerTime) {
        val list = mutableListOf<Particle>()
        val rnd = Random(triggerTime)
        val count = 8
        for (i in 0 until count) {
            val angle = rnd.nextDouble(0.0, Math.PI * 2)
            val speed = rnd.nextFloat() * 65f + 25f
            val color = when (effectType) {
                KeyboardEffectType.CYBER_CYAN -> listOf(Color(0xFF00F0FF), Color(0xFF38BDF8), Color(0xFF67E8F9), Color.White).random(rnd)
                KeyboardEffectType.NEON_GREEN -> listOf(Color(0xFF10B981), Color(0xFF22C55E), Color(0xFF4ADE80), Color(0xFF86EFAC)).random(rnd)
                else -> Color.White
            }
            list.add(
                Particle(
                    id = rnd.nextLong(),
                    startX = 0.5f,
                    startY = 0.5f,
                    angle = angle,
                    speed = speed,
                    color = color,
                    size = rnd.nextFloat() * 4f + 3f,
                    shape = "circle"
                )
            )
        }
        list
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val maxRadius = (size.width.coerceAtLeast(size.height)) * 0.9f
        val alpha = (1f - currentProgress).coerceIn(0f, 1f)

        when (effectType) {
            KeyboardEffectType.CYBER_CYAN -> {
                // Expanding neon glow ring
                val currentRadius = currentProgress * maxRadius
                val cyanRadius = (currentRadius + 10f).coerceAtLeast(1f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF00F0FF).copy(alpha = 0.55f * alpha), Color(0xFF00F0FF).copy(alpha = 0.1f * alpha), Color.Transparent),
                        center = Offset(centerX, centerY),
                        radius = cyanRadius
                    ),
                    radius = cyanRadius,
                    center = Offset(centerX, centerY)
                )
                drawCircle(
                    color = Color(0xFF38BDF8).copy(alpha = alpha),
                    radius = currentRadius,
                    center = Offset(centerX, centerY),
                    style = Stroke(width = 2.5f * (1f - currentProgress * 0.5f))
                )
            }
            KeyboardEffectType.NEON_GREEN -> {
                // Pulse Green Aura + Border flash
                val currentRadius = currentProgress * maxRadius * 0.85f
                val greenRadius = (currentRadius + 8f).coerceAtLeast(1f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF22C55E).copy(alpha = 0.6f * alpha), Color.Transparent),
                        center = Offset(centerX, centerY),
                        radius = greenRadius
                    ),
                    radius = greenRadius,
                    center = Offset(centerX, centerY)
                )
                drawRoundRect(
                    color = Color(0xFF4ADE80).copy(alpha = alpha * 0.8f),
                    size = size,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f),
                    style = Stroke(width = 3f * (1f - currentProgress))
                )
            }
            else -> {}
        }

        // Draw bursting particles
        particles.forEach { p ->
            val dist = p.speed * currentProgress
            val px = centerX + (cos(p.angle) * dist).toFloat()
            val py = centerY + (sin(p.angle) * dist).toFloat() - (currentProgress * 20f)

            drawCircle(
                color = p.color.copy(alpha = alpha),
                radius = p.size * (1f - currentProgress * 0.5f),
                center = Offset(px, py)
            )
        }
    }
}

/**
 * Preview Card Component for Keyboard Effects:
 * Shows a mini keyboard row with keys `q`, `w`, `e` and glowing animation effects!
 */
@Composable
fun EffectCardItem(
    effect: KeyboardEffectType,
    isSelected: Boolean,
    badge: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var simTriggerTime by remember { mutableStateOf(0L) }

    LaunchedEffect(isSelected) {
        while (true) {
            delay(1200)
            simTriggerTime = System.currentTimeMillis()
        }
    }

    val cardBg = when (effect) {
        KeyboardEffectType.NONE -> Color(0xFF1E293B)
        KeyboardEffectType.CYBER_CYAN -> Color(0xFF0C1F2C)
        KeyboardEffectType.NEON_GREEN -> Color(0xFF0C2419)
    }

    val keyBg = when (effect) {
        KeyboardEffectType.CYBER_CYAN -> Color(0xFF163242)
        KeyboardEffectType.NEON_GREEN -> Color(0xFF143B27)
        else -> Color(0xFF334155)
    }

    val keyBorderColor = when (effect) {
        KeyboardEffectType.CYBER_CYAN -> Color(0xFF00F0FF)
        KeyboardEffectType.NEON_GREEN -> Color(0xFF22C55E)
        else -> Color(0xFF64748B)
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.5.dp, keyBorderColor) else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF475569).copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 5.dp else 1.5.dp),
        modifier = modifier
            .width(114.dp)
            .height(84.dp)
            .clickable { onClick() }
            .testTag("effect_card_${effect.id}")
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (badge != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 4.dp, end = 5.dp)
                        .background(keyBorderColor.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                        .border(0.6.dp, keyBorderColor.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = badge,
                        fontSize = 7.5.sp,
                        color = keyBorderColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Mini 3-key Preview (q, w, e) matching keyboard keys
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp)
            ) {
                // Key 'q'
                Box(
                    modifier = Modifier
                        .size(width = 26.dp, height = 34.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(keyBg)
                        .border(0.8.dp, keyBorderColor.copy(alpha = 0.3f), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("q", color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                // Key 'w' (Active Animated Center Key with live tap effect)
                Box(
                    modifier = Modifier
                        .size(width = 28.dp, height = 36.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) keyBorderColor.copy(alpha = 0.28f) else keyBg)
                        .border(1.2.dp, keyBorderColor, RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    KeyEffectLayer(
                        effectType = effect,
                        triggerTime = simTriggerTime,
                        modifier = Modifier.fillMaxSize()
                    )

                    Text(
                        "w",
                        color = if (isSelected) keyBorderColor else Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Key 'e'
                Box(
                    modifier = Modifier
                        .size(width = 26.dp, height = 34.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(keyBg)
                        .border(0.8.dp, keyBorderColor.copy(alpha = 0.3f), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("e", color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // Checkmark overlay if selected
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 4.dp, start = 5.dp)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(keyBorderColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected Effect",
                        tint = Color.Black,
                        modifier = Modifier.size(11.dp)
                    )
                }
            }

            // Effect Name Pill at bottom
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 3.dp)
                    .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(5.dp))
                    .padding(horizontal = 6.dp, vertical = 1.5.dp)
            ) {
                Text(
                    text = effect.displayName,
                    fontSize = 8.5.sp,
                    color = Color(0xFFF1F5F9),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
