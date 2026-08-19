package com.example.ui.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

data class WallpaperPreset(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val gradientColors: List<Color>,
    val defaultDarkness: Float = 0.45f
)

object CustomWallpaperPresets {
    val PRESETS = listOf(
        WallpaperPreset(
            id = "tirupati_gold",
            title = "Tirupati Golden Sunset",
            description = "Sacred temple gold and twilight amber glow",
            emoji = "🛕",
            gradientColors = listOf(Color(0xFF451A03), Color(0xFFB45309), Color(0xFFD97706), Color(0xFF78350F)),
            defaultDarkness = 0.40f
        ),
        WallpaperPreset(
            id = "godavari_sunrise",
            title = "Godavari River Sunrise",
            description = "Serene sunrise over Andhra's legendary river",
            emoji = "🌅",
            gradientColors = listOf(Color(0xFF0C4A6E), Color(0xFF0284C7), Color(0xFFF97316), Color(0xFF7C2D12)),
            defaultDarkness = 0.45f
        ),
        WallpaperPreset(
            id = "charminar_twilight",
            title = "Charminar Twilight",
            description = "Historic Hyderabad night lights & royal violet",
            emoji = "🕌",
            gradientColors = listOf(Color(0xFF1E1B4B), Color(0xFF4338CA), Color(0xFF818CF8), Color(0xFF312E81)),
            defaultDarkness = 0.45f
        ),
        WallpaperPreset(
            id = "araku_valley",
            title = "Araku Valley Evergreen",
            description = "Lush misty green hills of Eastern Ghats",
            emoji = "🌿",
            gradientColors = listOf(Color(0xFF052E16), Color(0xFF166534), Color(0xFF22C55E), Color(0xFF14532D)),
            defaultDarkness = 0.40f
        ),
        WallpaperPreset(
            id = "tollywood_lights",
            title = "Tollywood Neon Glow",
            description = "Electric stage lights and cinema spectacle",
            emoji = "🎬",
            gradientColors = listOf(Color(0xFF3B0764), Color(0xFF9333EA), Color(0xFFF43F5E), Color(0xFF881337)),
            defaultDarkness = 0.50f
        ),
        WallpaperPreset(
            id = "festive_diwali",
            title = "Telugu Festive Diyas",
            description = "Warm glowing lamps and celebratory fireworks",
            emoji = "🪔",
            gradientColors = listOf(Color(0xFF4A0404), Color(0xFF991B1B), Color(0xFFF59E0B), Color(0xFF78350F)),
            defaultDarkness = 0.40f
        )
    )
}

@Composable
fun KeyboardBackgroundSurface(
    modifier: Modifier = Modifier,
    presetId: String?,
    customDarkness: Float,
    basePalette: KeyboardPalette,
    content: @Composable () -> Unit
) {
    val preset = CustomWallpaperPresets.PRESETS.find { it.id == presetId }

    if (preset != null) {
        Box(
            modifier = modifier
                .background(Brush.verticalGradient(preset.gradientColors))
        ) {
            // Darkness / contrast overlay for readability
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = customDarkness.coerceIn(0.1f, 0.85f)))
            )
            content()
        }
    } else {
        Box(
            modifier = modifier
                .background(basePalette.background)
        ) {
            content()
        }
    }
}
