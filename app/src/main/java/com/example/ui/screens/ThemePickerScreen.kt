package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.KeyboardPreferences
import com.example.data.KeyboardThemeType

data class ThemeColorItem(
    val id: String,
    val themeType: KeyboardThemeType,
    val name: String,
    val bg: Color,
    val text: Color,
    val accent: Color,
    val dots: List<Color> = emptyList()
)

data class ThemePatternItem(
    val id: String,
    val name: String,
    val gradient: Brush,
    val isDownloaded: Boolean = true
)

data class ThemeEffectItem(
    val id: String,
    val name: String,
    val keyBg: Color,
    val keyText: Color,
    val hasShadow: Boolean,
    val hasGlow: Boolean
)

@Composable
fun ThemePickerScreen(
    preferences: KeyboardPreferences,
    onUpdatePreferences: (KeyboardPreferences) -> Unit,
    isDarkMode: Boolean
) {
    val context = LocalContext.current
    var selectedThemeType by remember { mutableStateOf(preferences.themeType) }
    var showKeyBorders by remember { mutableStateOf(preferences.keyBorderEnabled) }

    // Color swatches (Matching Screenshot 6)
    val colorThemes = listOf(
        ThemeColorItem("light_slate", KeyboardThemeType.CLEAN_LIGHT, "Slate Light", Color(0xFFE2E8F0), Color(0xFF0F172A), Color(0xFF2563EB), listOf(Color(0xFF60A5FA), Color(0xFF3B82F6), Color(0xFF1D4ED8))),
        ThemeColorItem("ocean_blue", KeyboardThemeType.DESH_ROYAL_BLUE, "Ocean Blue", Color(0xFF1D4ED8), Color(0xFFFFFFFF), Color(0xFF60A5FA), listOf(Color(0xFF93C5FD), Color(0xFF60A5FA), Color(0xFFEF4444), Color(0xFFF59E0B))),
        ThemeColorItem("dark_slate", KeyboardThemeType.MIDNIGHT_SLATE, "Slate Dark", Color(0xFF1E293B), Color(0xFFF8FAFC), Color(0xFF38BDF8), listOf(Color(0xFF64748B), Color(0xFF475569), Color(0xFF0284C7))),
        ThemeColorItem("blush_pink", KeyboardThemeType.CHERRY_BLOSSOM_WHITE, "Blush Pink", Color(0xFFFCE7F3), Color(0xFF831843), Color(0xFFEC4899), listOf(Color(0xFFF472B6), Color(0xFFDB2777))),
        ThemeColorItem("forest_slate", KeyboardThemeType.EMERALD_GREEN, "Forest Green", Color(0xFF1C2D27), Color(0xFF34D399), Color(0xFF10B981), listOf(Color(0xFF10B981), Color(0xFFF59E0B), Color(0xFFEF4444))),
        ThemeColorItem("amoled_rainbow", KeyboardThemeType.PITCH_BLACK, "AMOLED Spectrum", Color(0xFF050811), Color(0xFF38BDF8), Color(0xFF06B6D4), listOf(Color(0xFF06B6D4), Color(0xFF10B981), Color(0xFFF59E0B), Color(0xFFEF4444), Color(0xFFEC4899)))
    )

    // Patterns & Gradients (Matching Screenshot 7)
    val patternThemes = listOf(
        ThemePatternItem(
            "pattern_poly", "Low-Poly Crystal",
            Brush.linearGradient(listOf(Color(0xFF9333EA), Color(0xFF3B82F6), Color(0xFF06B6D4)))
        ),
        ThemePatternItem(
            "pattern_mint", "Mint Breeze",
            Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF059669), Color(0xFF047857)))
        ),
        ThemePatternItem(
            "pattern_leather", "Leather Texture",
            Brush.linearGradient(listOf(Color(0xFF292524), Color(0xFF1C1917), Color(0xFF0C0A09)))
        ),
        ThemePatternItem(
            "pattern_abstract", "Oil Colors",
            Brush.linearGradient(listOf(Color(0xFF1E3A8A), Color(0xFFF59E0B), Color(0xFF10B981)))
        ),
        ThemePatternItem(
            "pattern_ocean", "Ocean Waves",
            Brush.linearGradient(listOf(Color(0xFF0891B2), Color(0xFF06B6D4), Color(0xFF22D3EE))),
            isDownloaded = false
        ),
        ThemePatternItem(
            "pattern_nature", "Floral Sunset",
            Brush.linearGradient(listOf(Color(0xFF0284C7), Color(0xFFEA580C), Color(0xFF7C2D12))),
            isDownloaded = false
        )
    )

    // Effects (Matching Screenshot 7)
    val effectThemes = listOf(
        ThemeEffectItem("fx_flat", "Standard Flat", Color(0xFF1E293B), Color.White, false, false),
        ThemeEffectItem("fx_raised", "Raised 3D Keys", Color(0xFFFFFFFF), Color(0xFF0F172A), true, false),
        ThemeEffectItem("fx_glow", "Neon Glow Keys", Color(0xFF0A0F1D), Color(0xFF38BDF8), false, true),
        ThemeEffectItem("fx_glass", "Frosted Keycaps", Color(0xFFF1F5F9), Color(0xFF1E293B), true, false)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 80.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .testTag("themes_screen_grid")
    ) {
        // Section 1: My themes (Dashed Add Photo Button - Matching Screenshot 6)
        item(span = { GridItemSpan(3) }) {
            Text(
                text = "My themes",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
            )
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(82.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isDarkMode) Color(0xFF161F30) else Color(0xFFF8FAFC))
                    .border(
                        1.5.dp,
                        if (isDarkMode) Color(0xFF475569) else Color(0xFF94A3B8),
                        RoundedCornerShape(10.dp)
                    )
                    .clickable {
                        Toast.makeText(context, "Choose photo from gallery for custom keyboard background", Toast.LENGTH_SHORT).show()
                    }
                    .testTag("add_photo_theme_card"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Add Photo",
                        tint = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Add Photo",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                }
            }
        }

        // Section 2: Default System Themes (Screenshot 6)
        item(span = { GridItemSpan(3) }) {
            Text(
                text = "Default",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 12.dp, bottom = 2.dp)
            )
        }

        // System Auto
        item {
            DefaultThemeCard(
                title = "System auto",
                isSelected = selectedThemeType == KeyboardThemeType.MIDNIGHT_SLATE,
                onClick = {
                    selectedThemeType = KeyboardThemeType.MIDNIGHT_SLATE
                    onUpdatePreferences(preferences.copy(themeType = KeyboardThemeType.MIDNIGHT_SLATE))
                }
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f).fillMaxSize().background(Color(0xFF1E293B)), contentAlignment = Alignment.Center) {
                        Text("Aa", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(modifier = Modifier.weight(1f).fillMaxSize().background(Color(0xFFE2E8F0)), contentAlignment = Alignment.Center) {
                        Text("Aa", color = Color(0xFF0F172A), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Dark
        item {
            DefaultThemeCard(
                title = "Dark",
                isSelected = selectedThemeType == KeyboardThemeType.PITCH_BLACK,
                onClick = {
                    selectedThemeType = KeyboardThemeType.PITCH_BLACK
                    onUpdatePreferences(preferences.copy(themeType = KeyboardThemeType.PITCH_BLACK))
                }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF0F172A)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Aa", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.width(36.dp).height(5.dp).background(Color(0xFF334155), RoundedCornerShape(2.dp)))
                    }
                }
            }
        }

        // Light
        item {
            DefaultThemeCard(
                title = "Light",
                isSelected = selectedThemeType == KeyboardThemeType.CLEAN_LIGHT,
                onClick = {
                    selectedThemeType = KeyboardThemeType.CLEAN_LIGHT
                    onUpdatePreferences(preferences.copy(themeType = KeyboardThemeType.CLEAN_LIGHT))
                }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Aa", color = Color(0xFF0F172A), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.width(36.dp).height(5.dp).background(Color(0xFFCBD5E1), RoundedCornerShape(2.dp)))
                    }
                }
            }
        }

        // Dynamic / Accent
        item {
            DefaultThemeCard(
                title = "Dynamic",
                isSelected = selectedThemeType == KeyboardThemeType.SAFFRON_ORANGE,
                onClick = {
                    selectedThemeType = KeyboardThemeType.SAFFRON_ORANGE
                    onUpdatePreferences(preferences.copy(themeType = KeyboardThemeType.SAFFRON_ORANGE))
                }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF271E18)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Aa", color = Color(0xFFFFDBC8), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.width(36.dp).height(5.dp).background(Color(0xFF5D4037), RoundedCornerShape(2.dp)))
                    }
                }
            }
        }

        // Section 3: Colors (Screenshot 6)
        item(span = { GridItemSpan(3) }) {
            Text(
                text = "Colors",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 12.dp, bottom = 2.dp)
            )
        }

        items(colorThemes) { colorItem ->
            val isSelected = selectedThemeType == colorItem.themeType
            Card(
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(82.dp)
                    .clickable {
                        selectedThemeType = colorItem.themeType
                        onUpdatePreferences(preferences.copy(themeType = colorItem.themeType))
                        Toast.makeText(context, "Applied ${colorItem.name} theme", Toast.LENGTH_SHORT).show()
                    },
                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF059669)) else null
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colorItem.bg)
                ) {
                    // Accent colored dots
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        colorItem.dots.forEach { dotColor ->
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(dotColor)
                            )
                        }
                    }

                    // Main preview Aa
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Aa",
                            color = colorItem.text,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(5.dp)
                                .background(colorItem.accent.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                        )
                    }

                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(4.dp)
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF059669)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.White,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    }
                }
            }
        }

        // Section 4: Effects (Screenshot 7)
        item(span = { GridItemSpan(3) }) {
            Text(
                text = "Effects",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 12.dp, bottom = 2.dp)
            )
        }

        items(effectThemes) { effect ->
            Card(
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(82.dp)
                    .clickable {
                        Toast.makeText(context, "Applied ${effect.name}", Toast.LENGTH_SHORT).show()
                    }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(if (isDarkMode) Color(0xFF0B101E) else Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("q", "w", "e").forEach { letter ->
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(effect.keyBg)
                                    .then(
                                        if (effect.hasGlow) Modifier.border(1.dp, Color(0xFF38BDF8), RoundedCornerShape(4.dp))
                                        else Modifier
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(letter, color = effect.keyText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Section 5: Patterns (Screenshot 7)
        item(span = { GridItemSpan(3) }) {
            Text(
                text = "Patterns & Gradients",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 12.dp, bottom = 2.dp)
            )
        }

        items(patternThemes) { pattern ->
            Card(
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(82.dp)
                    .clickable {
                        Toast.makeText(context, "Applied ${pattern.name}", Toast.LENGTH_SHORT).show()
                    }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(pattern.gradient),
                    contentAlignment = Alignment.Center
                ) {
                    if (!pattern.isDownloaded) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.35f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Download",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Section 6: Show Key Borders Toggle (Screenshot 6 & 7)
        item(span = { GridItemSpan(3) }) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Show key borders",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Switch(
                        checked = showKeyBorders,
                        onCheckedChange = { checked ->
                            showKeyBorders = checked
                            onUpdatePreferences(preferences.copy(keyBorderEnabled = checked))
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF047857)
                        ),
                        modifier = Modifier.testTag("switch_show_key_borders")
                    )
                }
            }
        }
    }
}

@Composable
fun DefaultThemeCard(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .clickable(onClick = onClick),
            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF059669)) else null
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                content()
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF059669)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = Color.White,
                            modifier = Modifier.size(11.dp)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
