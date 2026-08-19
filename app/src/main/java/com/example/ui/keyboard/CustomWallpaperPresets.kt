package com.example.ui.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

data class WallpaperPreset(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val gradientColors: List<Color>,
    val defaultDarkness: Float = 0.45f
)

data class KeyboardBackgroundImage(
    val id: String,
    val title: String,
    val category: String,
    val imageUrl: String,
    val defaultDarkness: Float = 0.45f
)

object CustomWallpaperPresets {
    // 15 Curated High Quality Background Images (Hillsations, Hills, Cars, Bikes, Buildings, Beaches)
    val BACKGROUND_IMAGES = listOf(
        // Category 1: Hill Stations
        KeyboardBackgroundImage(
            id = "bg_hillstation_munnar",
            title = "Munnar Tea Hills",
            category = "Hill Stations",
            imageUrl = "https://images.unsplash.com/photo-1544735716-392fe2489ffa?auto=format&fit=crop&w=800&q=80",
            defaultDarkness = 0.40f
        ),
        KeyboardBackgroundImage(
            id = "bg_hillstation_misty",
            title = "Misty Valley Lake",
            category = "Hill Stations",
            imageUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=800&q=80",
            defaultDarkness = 0.42f
        ),
        KeyboardBackgroundImage(
            id = "bg_hillstation_fog",
            title = "Pine Forest Mist",
            category = "Hill Stations",
            imageUrl = "https://images.unsplash.com/photo-1448375240586-882707db888b?auto=format&fit=crop&w=800&q=80",
            defaultDarkness = 0.45f
        ),

        // Category 2: Hills & Mountains
        KeyboardBackgroundImage(
            id = "bg_hills_green",
            title = "Lush Green Peaks",
            category = "Hills",
            imageUrl = "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=800&q=80",
            defaultDarkness = 0.40f
        ),
        KeyboardBackgroundImage(
            id = "bg_hills_snow",
            title = "Starry Alpine Summit",
            category = "Hills",
            imageUrl = "https://images.unsplash.com/photo-1519681393784-d120267933ba?auto=format&fit=crop&w=800&q=80",
            defaultDarkness = 0.45f
        ),
        KeyboardBackgroundImage(
            id = "bg_hills_sunset",
            title = "Sunset Mountain Glow",
            category = "Hills",
            imageUrl = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?auto=format&fit=crop&w=800&q=80",
            defaultDarkness = 0.40f
        ),

        // Category 3: Cars
        KeyboardBackgroundImage(
            id = "bg_cars_porsche",
            title = "Dark Sports Car",
            category = "Cars",
            imageUrl = "https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&w=800&q=80",
            defaultDarkness = 0.42f
        ),
        KeyboardBackgroundImage(
            id = "bg_cars_neon",
            title = "Neon Night Supercar",
            category = "Cars",
            imageUrl = "https://images.unsplash.com/photo-1617788138017-80ad40651399?auto=format&fit=crop&w=800&q=80",
            defaultDarkness = 0.40f
        ),

        // Category 4: Bikes
        KeyboardBackgroundImage(
            id = "bg_bikes_cruiser",
            title = "Modern Superbike",
            category = "Bikes",
            imageUrl = "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=800&q=80",
            defaultDarkness = 0.45f
        ),
        KeyboardBackgroundImage(
            id = "bg_bikes_racing",
            title = "Track Racing Bike",
            category = "Bikes",
            imageUrl = "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=800&q=80",
            defaultDarkness = 0.42f
        ),

        // Category 5: Buildings & Architecture
        KeyboardBackgroundImage(
            id = "bg_buildings_skyline",
            title = "Metropolis Skyline",
            category = "Buildings",
            imageUrl = "https://images.unsplash.com/photo-1477959858617-67f30bc75b82?auto=format&fit=crop&w=800&q=80",
            defaultDarkness = 0.45f
        ),
        KeyboardBackgroundImage(
            id = "bg_buildings_neon",
            title = "Cyber City Lights",
            category = "Buildings",
            imageUrl = "https://images.unsplash.com/photo-1514565131-fce0801e5785?auto=format&fit=crop&w=800&q=80",
            defaultDarkness = 0.42f
        ),
        KeyboardBackgroundImage(
            id = "bg_buildings_glass",
            title = "Modern High-Rise",
            category = "Buildings",
            imageUrl = "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?auto=format&fit=crop&w=800&q=80",
            defaultDarkness = 0.45f
        ),

        // Category 6: Beaches
        KeyboardBackgroundImage(
            id = "bg_beaches_turquoise",
            title = "Turquoise Ocean Wave",
            category = "Beaches",
            imageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=800&q=80",
            defaultDarkness = 0.40f
        ),
        KeyboardBackgroundImage(
            id = "bg_beaches_sunset",
            title = "Sunset Coast & Palms",
            category = "Beaches",
            imageUrl = "https://images.unsplash.com/photo-1510414842594-a61c69b5ae57?auto=format&fit=crop&w=800&q=80",
            defaultDarkness = 0.40f
        )
    )

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
    customPhotoUri: String? = null,
    customDarkness: Float,
    basePalette: KeyboardPalette,
    content: @Composable () -> Unit
) {
    val preset = CustomWallpaperPresets.PRESETS.find { it.id == presetId }

    if (customPhotoUri != null) {
        Box(modifier = modifier) {
            AsyncImage(
                model = customPhotoUri,
                contentDescription = "Custom Keyboard Wallpaper",
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )
            // Darkness / contrast overlay for readability
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = customDarkness.coerceIn(0.1f, 0.85f)))
            )
            content()
        }
    } else if (preset != null) {
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
