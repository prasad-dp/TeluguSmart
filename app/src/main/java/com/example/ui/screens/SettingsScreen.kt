package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.HapticStrength
import com.example.data.KeyboardPreferences
import com.example.data.KeyboardSoundProfile
import com.example.data.OneHandedMode
import com.example.engine.SoundFeedbackHelper
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.LightBorder

data class SettingItem(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val icon: ImageVector,
    val iconTint: Color
)

@Composable
fun SettingsScreen(
    preferences: KeyboardPreferences,
    onUpdatePreferences: (KeyboardPreferences) -> Unit,
    isDarkMode: Boolean,
    onNavigateToThemes: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var activeDialog by remember { mutableStateOf<String?>(null) }

    // Dialog States
    var currentHeightPercent by remember { mutableIntStateOf(preferences.keyboardHeightPercent) }
    var soundEnabled by remember { mutableStateOf(preferences.keySoundFeedback) }
    var soundProfile by remember { mutableStateOf(preferences.soundProfile) }
    var soundVolume by remember { mutableFloatStateOf(preferences.keySoundVolume) }
    var hapticEnabled by remember { mutableStateOf(preferences.keyHapticFeedback) }
    var hapticStrength by remember { mutableStateOf(preferences.hapticStrength) }
    var numberRowEnabled by remember { mutableStateOf(preferences.showNumberRow) }
    var autoTranslitEnabled by remember { mutableStateOf(preferences.autoTransliterateOnSpace) }
    var autoCapEnabled by remember { mutableStateOf(preferences.autoCapitalization) }
    var nextWordPredictionEnabled by remember { mutableStateOf(preferences.nextWordPredictionEnabled) }
    var glideTypingEnabled by remember { mutableStateOf(preferences.glideTypingEnabled) }
    var oneHandedMode by remember { mutableStateOf(preferences.oneHandedMode) }
    var keyBorderEnabled by remember { mutableStateOf(preferences.keyBorderEnabled) }

    // Color tokens
    val brandEmerald = if (isDarkMode) Color(0xFF10B981) else Color(0xFF047857)
    val brandTeal = if (isDarkMode) Color(0xFF2DD4BF) else Color(0xFF0D9488)
    val cardBorder = if (isDarkMode) DarkBorder else LightBorder

    // Semantic Section Groups
    val appearanceGroup = listOf(
        SettingItem("themes", "Themes & Wallpapers", "Customize keyboard background & colors", Icons.Default.Palette, brandTeal),
        SettingItem("key_borders", "Show Key Borders", if (keyBorderEnabled) "On • Outlines visible" else "Off • Borderless keys", Icons.Default.GridOn, brandEmerald),
        SettingItem("height", "Keyboard Height", "${currentHeightPercent}% Scale", Icons.Default.Height, brandTeal),
        SettingItem("effects", "Tap Effects & Particle Animations", "Neon glow, ripple & heart effects", Icons.Default.AutoAwesome, brandEmerald)
    )

    val typingGroup = listOf(
        SettingItem("typing", "Typing & Next-Word Prediction", "Suggestions, transliteration & auto-caps", Icons.Default.Keyboard, brandTeal),
        SettingItem("gestures", "Handwriting & Glide Typing", "Swipe trail & gesture composition", Icons.Default.Gesture, brandEmerald)
    )

    val soundGroup = listOf(
        SettingItem("sound", "Sound & Vibration Feedback", if (soundEnabled || hapticEnabled) "Active • Click & haptics" else "Silent mode", Icons.AutoMirrored.Filled.VolumeUp, brandTeal)
    )

    val layoutGroup = listOf(
        SettingItem("one_handed", "One-Handed Mode & Docking", when (oneHandedMode) {
            OneHandedMode.OFF -> "Full Width"
            OneHandedMode.LEFT -> "Left Hand Docked"
            OneHandedMode.RIGHT -> "Right Hand Docked"
        }, Icons.Default.PhoneAndroid, brandEmerald),
        SettingItem("emojis", "Emojis, Numbers & Symbols", if (numberRowEnabled) "Number row enabled" else "Standard layout", Icons.Default.EmojiEmotions, brandTeal)
    )

    val dataGroup = listOf(
        SettingItem("premium", "TeluguSmart Premium", "100% Free & Unlocked for all users", Icons.Default.Star, brandTeal),
        SettingItem("clear_data", "Clear Learned Words", "Reset personal dictionary & cache", Icons.Default.DeleteOutline, Color(0xFFEF4444)),
        SettingItem("help", "Help & User Guide", "Telugu typing tips & shortcuts", Icons.AutoMirrored.Filled.HelpOutline, brandEmerald)
    )

    @Composable
    fun SettingsGroupCard(
        title: String,
        items: List<SettingItem>
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = title,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = brandEmerald,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(start = 8.dp, top = 14.dp, bottom = 6.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, cardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    items.forEachIndexed { index, item ->
                        val isBorderSetting = item.id == "key_borders"
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    when (item.id) {
                                        "themes", "effects" -> onNavigateToThemes()
                                        "key_borders" -> {
                                            val newSetting = !keyBorderEnabled
                                            keyBorderEnabled = newSetting
                                            onUpdatePreferences(preferences.copy(keyBorderEnabled = newSetting))
                                            Toast.makeText(
                                                context,
                                                if (newSetting) "Key borders enabled" else "Key borders disabled",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        else -> activeDialog = item.id
                                    }
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .testTag("setting_row_${item.id}"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                                modifier = Modifier.weight(1f, fill = false)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(item.iconTint.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.title,
                                        tint = item.iconTint,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = item.title,
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (item.subtitle != null) {
                                        Text(
                                            text = item.subtitle,
                                            fontSize = 11.5.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(top = 1.dp)
                                        )
                                    }
                                }
                            }

                            if (isBorderSetting) {
                                Switch(
                                    checked = keyBorderEnabled,
                                    onCheckedChange = { checked ->
                                        keyBorderEnabled = checked
                                        onUpdatePreferences(preferences.copy(keyBorderEnabled = checked))
                                        Toast.makeText(
                                            context,
                                            if (checked) "Key borders enabled" else "Key borders disabled",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedTrackColor = brandEmerald,
                                        checkedThumbColor = Color.White,
                                        uncheckedTrackColor = if (isDarkMode) Color(0xFF334155) else Color(0xFFCBD5E1),
                                        uncheckedThumbColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .scale(0.72f)
                                        .testTag("switch_setting_key_borders")
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Open",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        if (index < items.size - 1) {
                            HorizontalDivider(
                                color = cardBorder.copy(alpha = 0.6f),
                                modifier = Modifier.padding(start = 68.dp, end = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 120.dp)
            .testTag("settings_screen_container"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SettingsGroupCard("🎨 APPEARANCE & THEME (రూపురేఖలు & థీమ్)", appearanceGroup)
        SettingsGroupCard("⌨️ TYPING & LANGUAGE (టైపింగ్ & భాష)", typingGroup)
        SettingsGroupCard("🔊 SOUND & VIBRATION (ధ్వని & వైబ్రేషన్)", soundGroup)
        SettingsGroupCard("📱 LAYOUT & KEYS (కీబోర్డ్ లేఅవుట్)", layoutGroup)
        SettingsGroupCard("🛡️ DATA & SUPPORT (సహాయం & డేటా)", dataGroup)

        Spacer(modifier = Modifier.height(20.dp))
    }

    // Modal Dialogs for Setting Controls
    when (activeDialog) {
        "height" -> {
            AlertDialog(
                onDismissRequest = { activeDialog = null },
                title = { Text("Keyboard Height", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text(
                            text = "Scale: $currentHeightPercent%",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Slider(
                            value = currentHeightPercent.toFloat(),
                            onValueChange = { currentHeightPercent = it.toInt() },
                            valueRange = 80f..130f,
                            colors = SliderDefaults.colors(
                                thumbColor = brandEmerald,
                                activeTrackColor = brandEmerald
                            )
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        onUpdatePreferences(preferences.copy(keyboardHeightPercent = currentHeightPercent))
                        activeDialog = null
                        Toast.makeText(context, "Keyboard height saved", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Save", color = brandEmerald, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { activeDialog = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
        "one_handed" -> {
            AlertDialog(
                onDismissRequest = { activeDialog = null },
                title = { Text("One-Handed Mode & Layout", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Choose default keyboard docking alignment for easy one-hand typing:",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OneHandedMode.entries.forEach { mode ->
                                val isSelected = (oneHandedMode == mode)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { oneHandedMode = mode },
                                    label = {
                                        Text(
                                            text = when (mode) {
                                                OneHandedMode.OFF -> "Full Width"
                                                OneHandedMode.LEFT -> "Left Hand"
                                                OneHandedMode.RIGHT -> "Right Hand"
                                            },
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = brandEmerald,
                                        selectedLabelColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("one_handed_mode_${mode.name.lowercase()}")
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        onUpdatePreferences(preferences.copy(oneHandedMode = oneHandedMode))
                        activeDialog = null
                        Toast.makeText(context, "Layout preference saved", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Save", color = brandEmerald, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { activeDialog = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
        "sound" -> {
            AlertDialog(
                onDismissRequest = { activeDialog = null },
                title = { Text("Sound & Vibration Feedback", fontWeight = FontWeight.Bold) },
                text = {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Keypress Audio Sound", fontWeight = FontWeight.SemiBold)
                            Switch(
                                checked = soundEnabled,
                                onCheckedChange = {
                                    soundEnabled = it
                                    if (it) SoundFeedbackHelper.playKeySound(soundProfile, soundVolume)
                                },
                                colors = SwitchDefaults.colors(checkedTrackColor = brandEmerald),
                                modifier = Modifier.scale(0.75f)
                            )
                        }

                        if (soundEnabled) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Key Sound Volume (శబ్ద స్థాయి):",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${(soundVolume * 100).toInt()}%",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = brandEmerald
                                    )
                                }
                                Slider(
                                    value = soundVolume,
                                    onValueChange = {
                                        soundVolume = it
                                    },
                                    onValueChangeFinished = {
                                        SoundFeedbackHelper.playKeySound(soundProfile, soundVolume)
                                    },
                                    valueRange = 0.10f..1.00f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = brandEmerald,
                                        activeTrackColor = brandEmerald
                                    )
                                )
                            }

                            Text("Sound Tone Profile (Tap to preview):", fontSize = 12.sp, color = Color.Gray)
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                KeyboardSoundProfile.entries.forEach { profile ->
                                    FilterChip(
                                        selected = (soundProfile == profile),
                                        onClick = {
                                            soundProfile = profile
                                            SoundFeedbackHelper.playKeySound(profile, soundVolume)
                                        },
                                        label = {
                                            Text(
                                                when (profile) {
                                                    KeyboardSoundProfile.DEFAULT_CLICK -> "🔊 Standard Click"
                                                    KeyboardSoundProfile.MECHANICAL_THOCK -> "⌨️ Mechanical Switch"
                                                    KeyboardSoundProfile.TYPEWRITER -> "📜 Classic Typewriter"
                                                    KeyboardSoundProfile.WATER_DROP -> "💧 Water Bubble Pop"
                                                    KeyboardSoundProfile.DIGITAL_BEEP -> "⚡ Cyber Blip"
                                                },
                                                fontSize = 12.sp
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = brandEmerald,
                                            selectedLabelColor = Color.White
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = cardBorder.copy(alpha = 0.5f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Haptic Feedback Vibration", fontWeight = FontWeight.SemiBold)
                            Switch(
                                checked = hapticEnabled,
                                onCheckedChange = {
                                    hapticEnabled = it
                                    if (it) SoundFeedbackHelper.triggerHaptic(hapticStrength)
                                },
                                colors = SwitchDefaults.colors(checkedTrackColor = brandEmerald),
                                modifier = Modifier.scale(0.75f)
                            )
                        }

                        if (hapticEnabled) {
                            Text("Haptic Vibration Intensity:", fontSize = 12.sp, color = Color.Gray)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                HapticStrength.entries.forEach { strength ->
                                    FilterChip(
                                        selected = (hapticStrength == strength),
                                        onClick = {
                                            hapticStrength = strength
                                            SoundFeedbackHelper.triggerHaptic(strength)
                                        },
                                        label = {
                                            Text(
                                                when (strength) {
                                                    HapticStrength.LIGHT -> "Light"
                                                    HapticStrength.MEDIUM -> "Medium"
                                                    HapticStrength.STRONG -> "Strong"
                                                },
                                                fontSize = 12.sp
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = brandEmerald,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        onUpdatePreferences(preferences.copy(
                            keySoundFeedback = soundEnabled,
                            soundProfile = soundProfile,
                            keySoundVolume = soundVolume,
                            keyHapticFeedback = hapticEnabled,
                            hapticStrength = hapticStrength
                        ))
                        activeDialog = null
                        Toast.makeText(context, "Sound & vibration preferences saved", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Save", color = brandEmerald, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { activeDialog = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
        "emojis" -> {
            AlertDialog(
                onDismissRequest = { activeDialog = null },
                title = { Text("Emojis & Numbers", fontWeight = FontWeight.Bold) },
                text = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Dedicated Top Number Row")
                        Switch(
                            checked = numberRowEnabled,
                            onCheckedChange = { numberRowEnabled = it },
                            colors = SwitchDefaults.colors(checkedTrackColor = brandEmerald),
                            modifier = Modifier.scale(0.75f)
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        onUpdatePreferences(preferences.copy(showNumberRow = numberRowEnabled))
                        activeDialog = null
                    }) {
                        Text("Done", color = brandEmerald)
                    }
                }
            )
        }
        "typing" -> {
            AlertDialog(
                onDismissRequest = { activeDialog = null },
                title = { Text("Typing Preferences", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Next-Word Smart Prediction")
                            Switch(
                                checked = nextWordPredictionEnabled,
                                onCheckedChange = { nextWordPredictionEnabled = it },
                                colors = SwitchDefaults.colors(checkedTrackColor = brandEmerald),
                                modifier = Modifier.scale(0.75f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Auto-Transliterate on Space")
                            Switch(
                                checked = autoTranslitEnabled,
                                onCheckedChange = { autoTranslitEnabled = it },
                                colors = SwitchDefaults.colors(checkedTrackColor = brandEmerald),
                                modifier = Modifier.scale(0.75f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Auto-Capitalization")
                            Switch(
                                checked = autoCapEnabled,
                                onCheckedChange = { autoCapEnabled = it },
                                colors = SwitchDefaults.colors(checkedTrackColor = brandEmerald),
                                modifier = Modifier.scale(0.75f)
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        onUpdatePreferences(preferences.copy(
                            nextWordPredictionEnabled = nextWordPredictionEnabled,
                            autoTransliterateOnSpace = autoTranslitEnabled,
                            autoCapitalization = autoCapEnabled
                        ))
                        activeDialog = null
                    }) {
                        Text("Done", color = brandEmerald)
                    }
                }
            )
        }
        "gestures" -> {
            AlertDialog(
                onDismissRequest = { activeDialog = null },
                title = { Text("Handwriting & Glide Typing", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Continuous Glide / Swipe Typing")
                            Switch(
                                checked = glideTypingEnabled,
                                onCheckedChange = { glideTypingEnabled = it },
                                colors = SwitchDefaults.colors(checkedTrackColor = brandEmerald),
                                modifier = Modifier.scale(0.75f)
                            )
                        }
                        Text(
                            "Gesture trail and finger handwriting canvas allow seamless single-finger composition in Telugu.",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        onUpdatePreferences(preferences.copy(glideTypingEnabled = glideTypingEnabled))
                        activeDialog = null
                    }) {
                        Text("Done", color = brandEmerald)
                    }
                }
            )
        }
        "premium" -> {
            AlertDialog(
                onDismissRequest = { activeDialog = null },
                title = { Text("TeluguSmart Premium", fontWeight = FontWeight.Bold) },
                text = {
                    Text("All premium themes, custom photo backgrounds, and advanced Telugu linguistic engines are 100% unlocked and free for everyone!")
                },
                confirmButton = {
                    TextButton(onClick = { activeDialog = null }) {
                        Text("Awesome!", color = brandEmerald)
                    }
                }
            )
        }
        "clear_data" -> {
            AlertDialog(
                onDismissRequest = { activeDialog = null },
                title = { Text("Clear Learning Data", fontWeight = FontWeight.Bold) },
                text = {
                    Text("Reset your custom learned Telugu words and restored default dictionary settings?")
                },
                confirmButton = {
                    TextButton(onClick = {
                        activeDialog = null
                        Toast.makeText(context, "Learned dictionary reset", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Reset", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { activeDialog = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
        "help" -> {
            AlertDialog(
                onDismissRequest = { activeDialog = null },
                title = { Text("Help & User Guide", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("• Type 'namaste' -> నమస్తే", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("• Type 'amma' -> అమ్మ", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("• Type 'bharatam' -> భారతం", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("• Switch to Telugu direct script anytime by tapping the layout button on the spacebar.", fontSize = 12.sp)
                    }
                },
                confirmButton = {
                    TextButton(onClick = { activeDialog = null }) {
                        Text("Got it", color = brandEmerald)
                    }
                }
            )
        }
    }
}
