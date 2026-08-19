package com.example.ui.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.TeluguSmartApplication
import com.example.data.KeyboardEffectType
import com.example.data.KeyboardPreferences
import com.example.data.KeyboardThemeType
import com.example.engine.TeluguTransliterationEngine
import com.example.ui.keyboard.CustomWallpaperPresets
import com.example.ui.keyboard.EffectCardItem
import com.example.ui.keyboard.KeyboardRootView
import com.example.ui.theme.BrandPrimary
import kotlinx.coroutines.launch

data class ThemeColorCardItem(
    val id: String,
    val themeType: KeyboardThemeType,
    val name: String,
    val bg: Color,
    val text: Color,
    val pillColor: Color,
    val defaultAccent: Color,
    val dots: List<Color>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemePickerScreen(
    preferences: KeyboardPreferences,
    onUpdatePreferences: (KeyboardPreferences) -> Unit,
    isDarkMode: Boolean
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val app = context.applicationContext as? TeluguSmartApplication
    val snippets by (app?.repository?.clipboardSnippets?.collectAsState(initial = emptyList())
        ?: remember { mutableStateOf(emptyList()) })

    var selectedThemeType by remember { mutableStateOf(preferences.themeType) }
    var showKeyBorders by remember { mutableStateOf(preferences.keyBorderEnabled) }
    var activeAccentColor by remember { 
        mutableStateOf<Color?>(preferences.customAccentColor?.let { Color(it) })
    }
    
    // Live Real-Time Keyboard Popup State (Starts false so user can browse all top features & effects comfortably)
    var isKeyboardPoppedUp by remember { mutableStateOf(false) }
    var testBufferText by remember { mutableStateOf("telugu") }

    // Selected Category Filter for Background Images
    var selectedBackgroundCategory by remember { mutableStateOf("All") }
    var downloadedImageIds by remember { 
        mutableStateOf(setOf("bg_hillstation_munnar", "bg_hills_green", "bg_cars_porsche")) 
    }

    // Custom Theme Customizer Sheet State
    var showCustomThemeSheet by remember { mutableStateOf(false) }
    var editingPhotoUri by remember { mutableStateOf<String?>(preferences.customPhotoUri) }
    var editingPresetId by remember { mutableStateOf<String?>(preferences.customPhotoPreset) }
    var editingDarkness by remember { mutableFloatStateOf(preferences.customPhotoDarkness) }
    var editingKeyOpacity by remember { mutableFloatStateOf(preferences.keyOpacity) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            editingPhotoUri = uri.toString()
            editingPresetId = null
            showCustomThemeSheet = true
        }
    }

    // THE 6 CORE MULTI-COLOR THEME CARDS (Matching user screenshot)
    val colorCards = listOf(
        // Card 1: Slate Light / Dynamic Grey (Aa + 3 dots: Blue, Emerald, Gray)
        ThemeColorCardItem(
            id = "slate_grey",
            themeType = KeyboardThemeType.CLEAN_LIGHT,
            name = "Slate Grey",
            bg = Color(0xFF8B95A5),
            text = Color(0xFF1E293B),
            pillColor = Color(0xFF717D91),
            defaultAccent = Color(0xFF2563EB),
            dots = listOf(Color(0xFF2563EB), Color(0xFF0D9488), Color(0xFF94A3B8))
        ),
        // Card 2: Royal Ocean Blue (Aa + 4 dots: Blue, Sky, Red, Amber)
        ThemeColorCardItem(
            id = "ocean_blue",
            themeType = KeyboardThemeType.DESH_ROYAL_BLUE,
            name = "Ocean Blue",
            bg = Color(0xFF1D4ED8),
            text = Color.White,
            pillColor = Color(0xFF3B82F6),
            defaultAccent = Color(0xFF2563EB),
            dots = listOf(Color(0xFF2563EB), Color(0xFF0284C7), Color(0xFFEF4444), Color(0xFFF59E0B))
        ),
        // Card 3: Slate Dark / Midnight (Aa + 3 dots: Cyan, Slate, Dark)
        ThemeColorCardItem(
            id = "slate_dark",
            themeType = KeyboardThemeType.MIDNIGHT_SLATE,
            name = "Slate Dark",
            bg = Color(0xFF1E293B),
            text = Color.White,
            pillColor = Color(0xFF334155),
            defaultAccent = Color(0xFF38BDF8),
            dots = listOf(Color(0xFF38BDF8), Color(0xFF64748B), Color(0xFF0F172A))
        ),
        // Card 4: Blush Soft Pink / Red (Aa + 2 dots: Rose, Red)
        ThemeColorCardItem(
            id = "blush_pink",
            themeType = KeyboardThemeType.CHERRY_BLOSSOM_WHITE,
            name = "Blush Soft",
            bg = Color(0xFFE2E8F0),
            text = Color(0xFFEF4444),
            pillColor = Color(0xFFFECACA),
            defaultAccent = Color(0xFFDC2626),
            dots = listOf(Color(0xFFF43F5E), Color(0xFFDC2626))
        ),
        // Card 5: Forest Green (Aa + 3 dots: Green, Amber, Red)
        ThemeColorCardItem(
            id = "forest_green",
            themeType = KeyboardThemeType.EMERALD_GREEN,
            name = "Forest Green",
            bg = Color(0xFF23322B),
            text = Color(0xFF22C55E),
            pillColor = Color(0xFF1B2421),
            defaultAccent = Color(0xFF22C55E),
            dots = listOf(Color(0xFF22C55E), Color(0xFFF59E0B), Color(0xFFEF4444))
        ),
        // Card 6: AMOLED Spectrum / Black (Aa + 6 rainbow dots: Cyan, Green, Orange, Red, Yellow, Purple)
        ThemeColorCardItem(
            id = "amoled_rainbow",
            themeType = KeyboardThemeType.PITCH_BLACK,
            name = "AMOLED Rainbow",
            bg = Color(0xFF070B12),
            text = Color(0xFF38BDF8),
            pillColor = Color(0xFF0F172A),
            defaultAccent = Color(0xFF06B6D4),
            dots = listOf(Color(0xFF06B6D4), Color(0xFF22C55E), Color(0xFFF97316), Color(0xFFEF4444), Color(0xFFEAB308), Color(0xFFA855F7))
        )
    )

    // Identify active selected card item
    val activeCardItem = colorCards.find { it.themeType == selectedThemeType } ?: colorCards.first()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 12.dp,
                bottom = if (isKeyboardPoppedUp) 350.dp else 120.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .testTag("themes_screen_grid")
        ) {
            // SECTION: COLORS TITLE
            item(span = { GridItemSpan(3) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Colors",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    if (!isKeyboardPoppedUp) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = BrandPrimary.copy(alpha = 0.12f),
                            modifier = Modifier.clickable { isKeyboardPoppedUp = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Keyboard, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(16.dp))
                                Text("Preview Keyboard", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = BrandPrimary)
                            }
                        }
                    }
                }
            }

            // THE 6 MULTI-COLOR THEME CARDS (2 rows of 3 columns)
            items(colorCards) { colorItem ->
                val isSelected = selectedThemeType == colorItem.themeType && preferences.customPhotoPreset == null && preferences.customPhotoUri == null
                
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(86.dp)
                        .clickable {
                            selectedThemeType = colorItem.themeType
                            activeAccentColor = colorItem.defaultAccent
                            val newAccentHex = colorItem.defaultAccent.toArgb().toLong()
                            onUpdatePreferences(
                                preferences.copy(
                                    themeType = colorItem.themeType,
                                    customAccentColor = newAccentHex,
                                    customPhotoPreset = null,
                                    customPhotoUri = null
                                )
                            )
                            isKeyboardPoppedUp = true
                            Toast.makeText(context, "Selected ${colorItem.name}", Toast.LENGTH_SHORT).show()
                        }
                        .testTag("color_theme_${colorItem.id}"),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.5.dp, BrandPrimary) else null,
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(colorItem.bg)
                    ) {
                        // Top-Right Color Dots (Multiple color variants in this card)
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 7.dp, end = 7.dp),
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            colorItem.dots.forEach { dotColor ->
                                Box(
                                    modifier = Modifier
                                        .size(6.5.dp)
                                        .clip(CircleShape)
                                        .background(dotColor)
                                )
                            }
                        }

                        // Center Aa text + spacebar indicator
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Aa",
                                color = colorItem.text,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Box(
                                modifier = Modifier
                                    .width(38.dp)
                                    .height(5.dp)
                                    .background(colorItem.pillColor, RoundedCornerShape(2.dp))
                            )
                        }

                        // Bottom-Right Accent Dot
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(bottom = 7.dp, end = 7.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isSelected && activeAccentColor != null) activeAccentColor!! else colorItem.defaultAccent)
                        )

                        // Center White Checkmark if Selected
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // SECTION: EFFECTS (Live Key Tap Glowing Feedback Effects)
            item(span = { GridItemSpan(3) }) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Key Tap Effects",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Choose an animation effect for key taps",
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (!isKeyboardPoppedUp) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BrandPrimary.copy(alpha = 0.12f),
                                modifier = Modifier.clickable { isKeyboardPoppedUp = true }
                            ) {
                                Text(
                                    text = "Test Live ⌨",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("effects_lazy_row")
                    ) {
                        // Top Industry Architecture Key Tap Effects + Clean Default
                        val curatedEffects = listOf(
                            Pair(KeyboardEffectType.MORPH_LIGATURE, "⚡ <4ms Morph"),
                            Pair(KeyboardEffectType.HAPTIC_3D_DEPTH, "🎯 <2ms 3D"),
                            Pair(KeyboardEffectType.GLASS_BLOOM, "✨ <6ms Bloom"),
                            Pair(KeyboardEffectType.SHADER_WAVE, "🌊 <8ms Wave"),
                            Pair(KeyboardEffectType.MICRO_PARTICLES, "🎆 <8ms Particles"),
                            Pair(KeyboardEffectType.CYBER_CYAN, "🔥 Popular"),
                            Pair(KeyboardEffectType.AMBER_FIRE, "✨ Festive"),
                            Pair(KeyboardEffectType.NEON_GREEN, "⚡ Neon"),
                            Pair(KeyboardEffectType.RAINBOW_WAVE, "🌈 Chroma"),
                            Pair(KeyboardEffectType.SPARKLE_STARS, "⭐ Golden"),
                            Pair(KeyboardEffectType.NONE, "Clean")
                        )
                        items(curatedEffects) { (effectItem, effectBadge) ->
                            val isEffectSelected = preferences.activeEffect == effectItem
                            EffectCardItem(
                                effect = effectItem,
                                isSelected = isEffectSelected,
                                badge = effectBadge,
                                onClick = {
                                    onUpdatePreferences(preferences.copy(activeEffect = effectItem))
                                    isKeyboardPoppedUp = true
                                    Toast.makeText(
                                        context,
                                        "${effectItem.displayName} applied • Tap keys below to test!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }
                    }
                }
            }

            // SECTION: MY THEMES & CUSTOM PHOTO STUDIO
            item(span = { GridItemSpan(3) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My themes (నా థీమ్స్)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (preferences.customPhotoPreset != null || preferences.customPhotoUri != null) {
                        Text(
                            text = "Customize",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandPrimary,
                            modifier = Modifier
                                .clickable {
                                    editingPhotoUri = preferences.customPhotoUri
                                    editingPresetId = preferences.customPhotoPreset
                                    editingDarkness = preferences.customPhotoDarkness
                                    editingKeyOpacity = preferences.keyOpacity
                                    showCustomThemeSheet = true
                                }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Add Custom Theme Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(86.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDarkMode) Color(0xFF161F30) else Color(0xFFF8FAFC))
                        .border(
                            1.5.dp,
                            BrandPrimary,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            editingPhotoUri = null
                            editingPresetId = editingPresetId ?: "tirupati_gold"
                            showCustomThemeSheet = true
                        }
                        .testTag("add_photo_theme_card"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(BrandPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Add Photo",
                                tint = BrandPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Custom Theme",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandPrimary
                        )
                    }
                }
            }

            // Custom Wallpaper Scenic Presets
            items(CustomWallpaperPresets.PRESETS.take(5)) { preset ->
                val isSelected = preferences.customPhotoPreset == preset.id && preferences.customPhotoUri == null
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(86.dp)
                        .clickable {
                            selectedThemeType = KeyboardThemeType.PITCH_BLACK
                            onUpdatePreferences(
                                preferences.copy(
                                    customPhotoPreset = preset.id,
                                    customPhotoUri = null,
                                    customPhotoDarkness = preset.defaultDarkness
                                )
                            )
                            isKeyboardPoppedUp = true
                            Toast.makeText(context, "Applied ${preset.title}", Toast.LENGTH_SHORT).show()
                        },
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, BrandPrimary) else androidx.compose.foundation.BorderStroke(1.dp, if (isDarkMode) Color(0xFF334155) else Color(0xFFCBD5E1))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.verticalGradient(preset.gradientColors))
                    ) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(Color.Black.copy(alpha = 0.35f))
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(preset.emoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = preset.title.split(" ").take(2).joinToString(" "),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }

                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(4.dp)
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(BrandPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Active",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }

            // SECTION: BACKGROUND IMAGES (Single Heading with 15 Curated Wallpapers)
            item(span = { GridItemSpan(3) }) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Background images",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "15 Wallpapers",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BrandPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Category Filter Pills
                    val categories = listOf("All", "Hill Stations", "Hills", "Cars", "Bikes", "Buildings", "Beaches")
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { cat ->
                            val isCatSelected = selectedBackgroundCategory == cat
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isCatSelected) BrandPrimary else if (isDarkMode) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                                border = if (!isCatSelected) androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isDarkMode) Color(0xFF334155) else Color(0xFFCBD5E1)
                                ) else null,
                                modifier = Modifier.clickable { selectedBackgroundCategory = cat }
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 12.sp,
                                    fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isCatSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Filtered 15 Background Images in 3-column Grid
            val filteredBackgrounds = if (selectedBackgroundCategory == "All") {
                CustomWallpaperPresets.BACKGROUND_IMAGES
            } else {
                CustomWallpaperPresets.BACKGROUND_IMAGES.filter { it.category == selectedBackgroundCategory }
            }

            items(filteredBackgrounds) { bgImage ->
                val isSelected = preferences.customPhotoUri == bgImage.imageUrl
                val isDownloaded = downloadedImageIds.contains(bgImage.id) || isSelected

                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(88.dp)
                        .clickable {
                            downloadedImageIds = downloadedImageIds + bgImage.id
                            selectedThemeType = KeyboardThemeType.PITCH_BLACK
                            onUpdatePreferences(
                                preferences.copy(
                                    customPhotoUri = bgImage.imageUrl,
                                    customPhotoPreset = null,
                                    customPhotoDarkness = bgImage.defaultDarkness,
                                    themeType = KeyboardThemeType.PITCH_BLACK
                                )
                            )
                            isKeyboardPoppedUp = true
                            Toast.makeText(context, "Applied ${bgImage.title} background", Toast.LENGTH_SHORT).show()
                        }
                        .testTag("bg_card_${bgImage.id}"),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.5.dp, BrandPrimary) else null,
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Background Wallpaper Image Loaded via Coil
                        AsyncImage(
                            model = bgImage.imageUrl,
                            contentDescription = bgImage.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Subtle darkening scrim for contrast
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.25f))
                        )

                        // Center Action: Checkmark if Selected, Download Icon if not
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(BrandPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.45f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = "Download and Set",
                                    tint = Color.White,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                        }

                        // Bottom Title Overlay
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.55f))
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = bgImage.title,
                                color = Color.White,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // REALTIME LIVE KEYBOARD POPUP AT THE BOTTOM
        AnimatedVisibility(
            visible = isKeyboardPoppedUp,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                    .testTag("theme_live_keyboard_container"),
                shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Header Bar with Realtime status and Hide button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isDarkMode) Color(0xFF1E293B) else Color(0xFFE2E8F0))
                            .padding(horizontal = 14.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            )
                            Text(
                                text = if (preferences.activeEffect != KeyboardEffectType.NONE) {
                                    "Live Preview • ${preferences.activeEffect.displayName} Effect • టైప్ చేయండి"
                                } else {
                                    "Live Keyboard Preview • టైప్ చేసి చూడండి"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkMode) Color(0xFF34D399) else Color(0xFF047857)
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Hide ✕",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandPrimary,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { isKeyboardPoppedUp = false }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Interactive Keyboard Root View showing theme in realtime
                    KeyboardRootView(
                        preferences = preferences,
                        currentBufferText = testBufferText,
                        onBufferTextChange = { newText ->
                            testBufferText = newText
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
                            isKeyboardPoppedUp = false
                        }
                    )
                }
            }
        }
    }

    // MODAL BOTTOM SHEET: CUSTOM THEME & PHOTO CREATOR
    if (showCustomThemeSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCustomThemeSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, bottom = 32.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Custom Theme Studio",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Choose your photo or scenic Telugu background",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { showCustomThemeSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // LIVE MINI KEYBOARD PREVIEW OVER BACKGROUND
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (editingPhotoUri != null) {
                            AsyncImage(
                                model = editingPhotoUri,
                                contentDescription = "Custom Image Preview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            val activePreset = CustomWallpaperPresets.PRESETS.find { it.id == (editingPresetId ?: "tirupati_gold") }
                                ?: CustomWallpaperPresets.PRESETS.first()
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Brush.verticalGradient(activePreset.gradientColors))
                            )
                        }

                        // Darkness filter
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = editingDarkness.coerceIn(0.1f, 0.85f)))
                        )

                        // Simulated keyboard keys over background
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                listOf("అ","ఆ","ఇ","ఈ","ఉ","ఊ","ఋ").forEach { char ->
                                    Surface(
                                        color = Color.White.copy(alpha = editingKeyOpacity),
                                        shape = RoundedCornerShape(4.dp),
                                        border = if (showKeyBorders) androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.2f)) else null,
                                        modifier = Modifier.size(width = 38.dp, height = 26.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(char, color = Color(0xFF0F172A), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                listOf("క","ఖ","గ","ఘ","ఙ","చ").forEach { char ->
                                    Surface(
                                        color = Color.White.copy(alpha = editingKeyOpacity),
                                        shape = RoundedCornerShape(4.dp),
                                        border = if (showKeyBorders) androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.2f)) else null,
                                        modifier = Modifier.size(width = 42.dp, height = 26.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(char, color = Color(0xFF0F172A), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Surface(
                                    color = Color.White.copy(alpha = editingKeyOpacity),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.size(width = 160.dp, height = 24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("స్పేస్ (Space)", color = Color(0xFF475569), fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Background Source Options
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            photoPickerLauncher.launch("image/*")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (editingPhotoUri != null) BrandPrimary else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = null,
                            tint = if (editingPhotoUri != null) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Pick Gallery Photo",
                            fontSize = 11.5.sp,
                            color = if (editingPhotoUri != null) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (editingPhotoUri != null) {
                        IconButton(
                            onClick = {
                                editingPhotoUri = null
                                editingPresetId = "tirupati_gold"
                            },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Clear Photo", tint = Color(0xFFEF4444))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Telugu Heritage & Nature Wallpapers:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CustomWallpaperPresets.PRESETS.forEach { preset ->
                        val isChosen = editingPresetId == preset.id && editingPhotoUri == null
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            border = if (isChosen) androidx.compose.foundation.BorderStroke(2.dp, BrandPrimary) else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF64748B).copy(alpha = 0.3f)),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .clickable {
                                    editingPhotoUri = null
                                    editingPresetId = preset.id
                                    editingDarkness = preset.defaultDarkness
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Brush.verticalGradient(preset.gradientColors)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(preset.emoji, fontSize = 16.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Brightness / Darkness Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Background Dimming (చీకటి స్థాయి):",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${(editingDarkness * 100).toInt()}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandPrimary
                    )
                }
                Slider(
                    value = editingDarkness,
                    onValueChange = { editingDarkness = it },
                    valueRange = 0.10f..0.85f,
                    colors = SliderDefaults.colors(
                        thumbColor = BrandPrimary,
                        activeTrackColor = BrandPrimary
                    )
                )

                // Keycap Transparency Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Keycap Opacity (కీల అపారదర్శకత):",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${(editingKeyOpacity * 100).toInt()}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandPrimary
                    )
                }
                Slider(
                    value = editingKeyOpacity,
                    onValueChange = { editingKeyOpacity = it },
                    valueRange = 0.40f..1.00f,
                    colors = SliderDefaults.colors(
                        thumbColor = BrandPrimary,
                        activeTrackColor = BrandPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Apply Theme Button
                Button(
                    onClick = {
                        onUpdatePreferences(
                            preferences.copy(
                                customPhotoUri = editingPhotoUri,
                                customPhotoPreset = if (editingPhotoUri == null) (editingPresetId ?: "tirupati_gold") else null,
                                customPhotoDarkness = editingDarkness,
                                keyOpacity = editingKeyOpacity
                            )
                        )
                        showCustomThemeSheet = false
                        isKeyboardPoppedUp = true
                        Toast.makeText(context, "Custom Photo Theme Applied Successfully!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("apply_custom_theme_btn")
                ) {
                    Text(
                        text = "Apply My Theme (థీమ్ వర్తింపజేయండి)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
