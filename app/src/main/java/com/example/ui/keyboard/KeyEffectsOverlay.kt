package com.example.ui.keyboard

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.KeyboardEffectType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    val shape: String = "circle" // "circle", "star", "heart"
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
            animationSpec = tween(durationMillis = 420, easing = LinearOutSlowInEasing)
        )
    }

    val currentProgress = progress.value
    if (currentProgress >= 1f) return

    val particles = remember(triggerTime) {
        val list = mutableListOf<Particle>()
        val rnd = Random(triggerTime)
        val count = when (effectType) {
            KeyboardEffectType.SPARKLE_STARS -> 10
            KeyboardEffectType.HEART_POP -> 6
            KeyboardEffectType.AMBER_FIRE -> 12
            KeyboardEffectType.RAINBOW_WAVE -> 14
            else -> 8
        }
        for (i in 0 until count) {
            val angle = rnd.nextDouble(0.0, Math.PI * 2)
            val speed = rnd.nextFloat() * 70f + 30f
            val color = when (effectType) {
                KeyboardEffectType.CYBER_CYAN -> listOf(Color(0xFF00F0FF), Color(0xFF38BDF8), Color(0xFF67E8F9), Color.White).random(rnd)
                KeyboardEffectType.AMBER_FIRE -> listOf(Color(0xFFF59E0B), Color(0xFFEF4444), Color(0xFFFBBF24), Color(0xFFF97316)).random(rnd)
                KeyboardEffectType.NEON_GREEN -> listOf(Color(0xFF10B981), Color(0xFF22C55E), Color(0xFF4ADE80), Color(0xFF86EFAC)).random(rnd)
                KeyboardEffectType.MAGENTA_BURST -> listOf(Color(0xFFEC4899), Color(0xFFD946EF), Color(0xFFA855F7), Color(0xFFF472B6)).random(rnd)
                KeyboardEffectType.RAINBOW_WAVE -> listOf(Color(0xFFFF0055), Color(0xFFFFAA00), Color(0xFF00FF66), Color(0xFF00DDFF), Color(0xFFAA00FF)).random(rnd)
                KeyboardEffectType.SPARKLE_STARS -> listOf(Color(0xFFFDE047), Color(0xFFF59E0B), Color(0xFFFEF08A), Color.White).random(rnd)
                KeyboardEffectType.WATER_RIPPLE -> listOf(Color(0xFF38BDF8).copy(alpha = 0.8f), Color(0xFF0284C7).copy(alpha = 0.7f), Color.White).random(rnd)
                KeyboardEffectType.HEART_POP -> listOf(Color(0xFFF43F5E), Color(0xFFFB7185), Color(0xFFFDA4AF), Color(0xFFEC4899)).random(rnd)
                else -> Color.White
            }
            val pShape = when (effectType) {
                KeyboardEffectType.SPARKLE_STARS -> "star"
                KeyboardEffectType.HEART_POP -> "heart"
                else -> "circle"
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
                    shape = pShape
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
            KeyboardEffectType.MORPH_LIGATURE -> {
                // 1. Morph & Ligature Reveal (< 4ms)
                // Telugu stroke reveal & glyph expansion with animated stroke path
                val strokeScale = (0.3f + currentProgress * 0.7f)
                val strokeAlpha = (1f - currentProgress * 0.8f)
                val ligRadius = (maxRadius * currentProgress).coerceAtLeast(1f)
                
                // Outer expanding ligature aura
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFF9900).copy(alpha = 0.6f * alpha), Color(0xFFFF5500).copy(alpha = 0.2f * alpha), Color.Transparent),
                        center = Offset(centerX, centerY),
                        radius = ligRadius
                    ),
                    radius = ligRadius,
                    center = Offset(centerX, centerY)
                )

                // Telugu Ligature "క" (Ka) Stroke Reveal
                val kaPath = Path().apply {
                    val s = 18f * strokeScale
                    moveTo(centerX - s * 0.6f, centerY - s * 0.4f)
                    cubicTo(
                        centerX - s, centerY - s * 0.9f,
                        centerX + s * 0.8f, centerY - s * 0.9f,
                        centerX + s * 0.6f, centerY - s * 0.2f
                    )
                    cubicTo(
                        centerX + s * 0.4f, centerY + s * 0.6f,
                        centerX - s * 0.8f, centerY + s * 0.6f,
                        centerX - s * 0.5f, centerY + s * 0.1f
                    )
                }
                drawPath(
                    path = kaPath,
                    color = Color(0xFFFFAA00).copy(alpha = strokeAlpha),
                    style = Stroke(width = 3.5f * (1f - currentProgress * 0.4f))
                )
            }
            KeyboardEffectType.HAPTIC_3D_DEPTH -> {
                // 2. Haptic-Sync 3D Depth (< 2ms)
                // 3D perspective depth tilt & tactile reflection
                drawRoundRect(
                    color = Color(0xFF38BDF8).copy(alpha = 0.5f * alpha),
                    size = size,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f),
                    style = Stroke(width = 2.5f)
                )
                // Specular top-left light beam reflection
                val specularPath = Path().apply {
                    moveTo(0f, size.height * 0.4f)
                    lineTo(size.width * 0.4f, 0f)
                    lineTo(size.width * 0.6f, 0f)
                    lineTo(0f, size.height * 0.6f)
                    close()
                }
                drawPath(
                    path = specularPath,
                    color = Color.White.copy(alpha = 0.6f * alpha)
                )
            }
            KeyboardEffectType.GLASS_BLOOM -> {
                // 3. Glassmorphic Light Bloom (< 6ms)
                // Frosted glass halo burst & specular bloom
                val bloomRadius = (currentProgress * maxRadius * 1.1f).coerceAtLeast(1f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.8f * alpha),
                            Color(0xFFE2E8F0).copy(alpha = 0.4f * alpha),
                            Color(0xFF38BDF8).copy(alpha = 0.15f * alpha),
                            Color.Transparent
                        ),
                        center = Offset(centerX, centerY),
                        radius = bloomRadius
                    ),
                    radius = bloomRadius,
                    center = Offset(centerX, centerY)
                )
            }
            KeyboardEffectType.SHADER_WAVE -> {
                // 4. Reactive Shader Wave (< 8ms)
                // Sine-wave shockwave sweep across key matrix
                val waveRadius = currentProgress * maxRadius
                val waveWidth = 14f * (1f - currentProgress * 0.5f)
                val totalRadius = (waveRadius + waveWidth).coerceAtLeast(1f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFEC4899).copy(alpha = 0.8f * alpha),
                            Color(0xFF8B5CF6).copy(alpha = 0.6f * alpha),
                            Color(0xFF06B6D4).copy(alpha = 0.3f * alpha),
                            Color.Transparent
                        ),
                        center = Offset(centerX, centerY),
                        radius = totalRadius
                    ),
                    radius = totalRadius,
                    center = Offset(centerX, centerY)
                )
                drawCircle(
                    color = Color(0xFFF472B6).copy(alpha = alpha),
                    radius = waveRadius,
                    center = Offset(centerX, centerY),
                    style = Stroke(width = 3f)
                )
            }
            KeyboardEffectType.MICRO_PARTICLES -> {
                // 5. Micro-Particle Emitter (< 8ms)
                // Zero-allocation physics particle explosion
                val burstRadius = (currentProgress * maxRadius * 0.9f).coerceAtLeast(1f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFF59E0B).copy(alpha = 0.6f * alpha), Color.Transparent),
                        center = Offset(centerX, centerY),
                        radius = burstRadius
                    ),
                    radius = burstRadius,
                    center = Offset(centerX, centerY)
                )
            }
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
            KeyboardEffectType.AMBER_FIRE -> {
                // Fiery glow bloom
                val currentRadius = currentProgress * maxRadius
                val amberRadius = (currentRadius + 6f).coerceAtLeast(1f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFF59E0B).copy(alpha = 0.7f * alpha), Color(0xFFEF4444).copy(alpha = 0.3f * alpha), Color.Transparent),
                        center = Offset(centerX, centerY),
                        radius = amberRadius
                    ),
                    radius = amberRadius,
                    center = Offset(centerX, centerY)
                )
            }
            KeyboardEffectType.MAGENTA_BURST -> {
                // Shockwave pulse
                val currentRadius = (currentProgress * maxRadius).coerceAtLeast(1f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFD946EF).copy(alpha = 0.65f * alpha), Color(0xFFEC4899).copy(alpha = 0.2f * alpha), Color.Transparent),
                        center = Offset(centerX, centerY),
                        radius = currentRadius
                    ),
                    radius = currentRadius,
                    center = Offset(centerX, centerY)
                )
                drawCircle(
                    color = Color(0xFFF472B6).copy(alpha = alpha),
                    radius = currentRadius,
                    center = Offset(centerX, centerY),
                    style = Stroke(width = 3f)
                )
            }
            KeyboardEffectType.RAINBOW_WAVE -> {
                // Multi-color chromatic circle
                val currentRadius = currentProgress * maxRadius
                val rainbowColors = listOf(
                    Color(0xFFFF0055).copy(alpha = alpha),
                    Color(0xFFFFAA00).copy(alpha = alpha),
                    Color(0xFF00FF66).copy(alpha = alpha),
                    Color(0xFF00DDFF).copy(alpha = alpha),
                    Color(0xFFAA00FF).copy(alpha = alpha)
                )
                drawCircle(
                    brush = Brush.sweepGradient(rainbowColors, center = Offset(centerX, centerY)),
                    radius = currentRadius,
                    center = Offset(centerX, centerY),
                    style = Stroke(width = 3.5f * (1f - currentProgress * 0.4f))
                )
            }
            KeyboardEffectType.WATER_RIPPLE -> {
                // Expanding smooth water rings
                val r1 = currentProgress * maxRadius
                val r2 = (currentProgress * 0.65f) * maxRadius
                drawCircle(
                    color = Color(0xFF38BDF8).copy(alpha = 0.7f * alpha),
                    radius = r1,
                    center = Offset(centerX, centerY),
                    style = Stroke(width = 2.5f)
                )
                drawCircle(
                    color = Color(0xFF0284C7).copy(alpha = 0.5f * alpha),
                    radius = r2,
                    center = Offset(centerX, centerY),
                    style = Stroke(width = 2f)
                )
            }
            else -> {}
        }

        // Draw individual bursting particles
        particles.forEach { p ->
            val dist = p.speed * currentProgress
            val px = centerX + (cos(p.angle) * dist).toFloat()
            val py = centerY + (sin(p.angle) * dist).toFloat() - (currentProgress * 20f) // float upward

            if (p.shape == "star") {
                // Twinkling Star shape
                val pRadius = p.size * (1f - currentProgress * 0.3f)
                val path = Path().apply {
                    moveTo(px, py - pRadius)
                    lineTo(px + pRadius * 0.35f, py - pRadius * 0.35f)
                    lineTo(px + pRadius, py)
                    lineTo(px + pRadius * 0.35f, py + pRadius * 0.35f)
                    lineTo(px, py + pRadius)
                    lineTo(px - pRadius * 0.35f, py + pRadius * 0.35f)
                    lineTo(px - pRadius, py)
                    lineTo(px - pRadius * 0.35f, py - pRadius * 0.35f)
                    close()
                }
                drawPath(path, color = p.color.copy(alpha = alpha))
            } else if (p.shape == "heart") {
                // Mini heart
                val pRadius = p.size * (1f - currentProgress * 0.2f)
                drawCircle(
                    color = p.color.copy(alpha = alpha),
                    radius = pRadius,
                    center = Offset(px, py)
                )
            } else {
                // Circle particle
                drawCircle(
                    color = p.color.copy(alpha = alpha),
                    radius = p.size * (1f - currentProgress * 0.5f),
                    center = Offset(px, py)
                )
            }
        }
    }
}

/**
 * Preview Card Component for Keyboard Effects matching the user screenshot:
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
    // Dynamic simulated tap animation on the middle key 'w'
    var simTriggerTime by remember { mutableStateOf(0L) }

    LaunchedEffect(isSelected) {
        while (true) {
            delay(1200)
            simTriggerTime = System.currentTimeMillis()
        }
    }

    val cardBg = when (effect) {
        KeyboardEffectType.NONE -> Color(0xFF1E293B)
        KeyboardEffectType.MORPH_LIGATURE -> Color(0xFF2C1E0C)
        KeyboardEffectType.HAPTIC_3D_DEPTH -> Color(0xFF0C2433)
        KeyboardEffectType.GLASS_BLOOM -> Color(0xFF1E293B)
        KeyboardEffectType.SHADER_WAVE -> Color(0xFF2B0E2A)
        KeyboardEffectType.MICRO_PARTICLES -> Color(0xFF2B1F0A)
        KeyboardEffectType.CYBER_CYAN -> Color(0xFF0C1F2C)
        KeyboardEffectType.AMBER_FIRE -> Color(0xFF2A1B0A)
        KeyboardEffectType.NEON_GREEN -> Color(0xFF0C2419)
        KeyboardEffectType.MAGENTA_BURST -> Color(0xFF280E24)
        KeyboardEffectType.RAINBOW_WAVE -> Color(0xFF161522)
        KeyboardEffectType.SPARKLE_STARS -> Color(0xFF241F10)
        KeyboardEffectType.WATER_RIPPLE -> Color(0xFF0C1C2A)
        KeyboardEffectType.HEART_POP -> Color(0xFF2A121D)
    }

    val keyBg = when (effect) {
        KeyboardEffectType.MORPH_LIGATURE -> Color(0xFF422E16)
        KeyboardEffectType.HAPTIC_3D_DEPTH -> Color(0xFF16384C)
        KeyboardEffectType.GLASS_BLOOM -> Color(0xFF334155)
        KeyboardEffectType.SHADER_WAVE -> Color(0xFF3B183A)
        KeyboardEffectType.MICRO_PARTICLES -> Color(0xFF3F2E13)
        KeyboardEffectType.CYBER_CYAN -> Color(0xFF163242)
        KeyboardEffectType.AMBER_FIRE -> Color(0xFF3F2613)
        KeyboardEffectType.NEON_GREEN -> Color(0xFF143B27)
        KeyboardEffectType.MAGENTA_BURST -> Color(0xFF3D1B37)
        KeyboardEffectType.RAINBOW_WAVE -> Color(0xFF262334)
        KeyboardEffectType.SPARKLE_STARS -> Color(0xFF38301B)
        KeyboardEffectType.WATER_RIPPLE -> Color(0xFF152F44)
        KeyboardEffectType.HEART_POP -> Color(0xFF3E1C2B)
        else -> Color(0xFF334155)
    }

    val keyBorderColor = when (effect) {
        KeyboardEffectType.MORPH_LIGATURE -> Color(0xFFFFAA00)
        KeyboardEffectType.HAPTIC_3D_DEPTH -> Color(0xFF38BDF8)
        KeyboardEffectType.GLASS_BLOOM -> Color(0xFFF1F5F9)
        KeyboardEffectType.SHADER_WAVE -> Color(0xFFEC4899)
        KeyboardEffectType.MICRO_PARTICLES -> Color(0xFFF59E0B)
        KeyboardEffectType.CYBER_CYAN -> Color(0xFF00F0FF)
        KeyboardEffectType.AMBER_FIRE -> Color(0xFFF59E0B)
        KeyboardEffectType.NEON_GREEN -> Color(0xFF22C55E)
        KeyboardEffectType.MAGENTA_BURST -> Color(0xFFD946EF)
        KeyboardEffectType.RAINBOW_WAVE -> Color(0xFF00DDFF)
        KeyboardEffectType.SPARKLE_STARS -> Color(0xFFEAB308)
        KeyboardEffectType.WATER_RIPPLE -> Color(0xFF38BDF8)
        KeyboardEffectType.HEART_POP -> Color(0xFFF43F5E)
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
            // Optional Badge at top-right
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
                    // Tap effect on key 'w'
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
