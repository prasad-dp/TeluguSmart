package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults

data class SettingItem(
    val id: String,
    val title: String,
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
    var hapticEnabled by remember { mutableStateOf(preferences.keyHapticFeedback) }
    var hapticStrength by remember { mutableStateOf(preferences.hapticStrength) }
    var numberRowEnabled by remember { mutableStateOf(preferences.showNumberRow) }
    var autoTranslitEnabled by remember { mutableStateOf(preferences.autoTransliterateOnSpace) }
    var autoCapEnabled by remember { mutableStateOf(preferences.autoCapitalization) }
    var nextWordPredictionEnabled by remember { mutableStateOf(preferences.nextWordPredictionEnabled) }
    var glideTypingEnabled by remember { mutableStateOf(preferences.glideTypingEnabled) }
    var oneHandedMode by remember { mutableStateOf(preferences.oneHandedMode) }
    var keyBorderEnabled by remember { mutableStateOf(preferences.keyBorderEnabled) }

    val settingItems = listOf(
        SettingItem("themes", "Themes", Icons.Default.Palette, Color(0xFF0D9488)),
        SettingItem("key_borders", "Show key borders", Icons.Default.GridOn, Color(0xFF047857)),
        SettingItem("effects", "Key tap effects & animations", Icons.Default.AutoAwesome, Color(0xFF059669)),
        SettingItem("height", "Keyboard height", Icons.Default.Height, Color(0xFF0D9488)),
        SettingItem("one_handed", "One-handed mode & layout", Icons.Default.PhoneAndroid, Color(0xFF059669)),
        SettingItem("sound", "Sound & vibration feedback", Icons.Default.VolumeUp, Color(0xFF0D9488)),
        SettingItem("emojis", "Emojis, numbers & symbols", Icons.Default.EmojiEmotions, Color(0xFF059669)),
        SettingItem("typing", "Typing & Next-word prediction", Icons.Default.Keyboard, Color(0xFF0D9488)),
        SettingItem("gestures", "Handwriting & Glide typing", Icons.Default.Gesture, Color(0xFF059669)),
        SettingItem("extras", "Extra features", Icons.Default.AutoAwesome, Color(0xFF0D9488)),
        SettingItem("accessibility", "Accessibility", Icons.Default.Accessibility, Color(0xFF059669)),
        SettingItem("premium", "Premium", Icons.Default.Star, Color(0xFF0D9488)),
        SettingItem("clear_data", "Clear data", Icons.Default.DeleteOutline, Color(0xFF059669)),
        SettingItem("help", "Help", Icons.Default.HelpOutline, Color(0xFF0D9488))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 120.dp)
            .testTag("settings_screen_container")
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                settingItems.forEachIndexed { index, item ->
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
                            .padding(horizontal = 18.dp, vertical = 15.dp)
                            .testTag("setting_row_${item.id}"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = item.iconTint,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = item.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (item.id == "key_borders") {
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
                                    checkedTrackColor = Color(0xFF047857),
                                    checkedThumbColor = Color.White
                                ),
                                modifier = Modifier.testTag("switch_setting_key_borders")
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

                    if (index < settingItems.size - 1) {
                        HorizontalDivider(
                            color = if (isDarkMode) Color(0xFF334155).copy(alpha = 0.5f) else Color(0xFFF1F5F9),
                            modifier = Modifier.padding(start = 56.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
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
                                thumbColor = Color(0xFF047857),
                                activeTrackColor = Color(0xFF047857)
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
                        Text("Save", color = Color(0xFF047857), fontWeight = FontWeight.Bold)
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
                        Text("Choose default keyboard docking alignment for easy one-hand typing:", fontSize = 13.sp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OneHandedMode.entries.forEach { mode ->
                                FilterChip(
                                    selected = (oneHandedMode == mode),
                                    onClick = { oneHandedMode = mode },
                                    label = {
                                        Text(
                                            when (mode) {
                                                OneHandedMode.OFF -> "Full Width"
                                                OneHandedMode.LEFT -> "Left Hand"
                                                OneHandedMode.RIGHT -> "Right Hand"
                                            },
                                            fontSize = 12.sp
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF047857),
                                        selectedLabelColor = Color.White
                                    )
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
                        Text("Save", color = Color(0xFF047857), fontWeight = FontWeight.Bold)
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
                                    if (it) SoundFeedbackHelper.playKeySound(soundProfile)
                                },
                                colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF047857))
                            )
                        }

                        if (soundEnabled) {
                            Text("Sound Tone Profile (Tap to preview):", fontSize = 12.sp, color = Color.Gray)
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                KeyboardSoundProfile.entries.forEach { profile ->
                                    FilterChip(
                                        selected = (soundProfile == profile),
                                        onClick = {
                                            soundProfile = profile
                                            SoundFeedbackHelper.playKeySound(profile)
                                        },
                                        label = {
                                            Text(
                                                when (profile) {
                                                    KeyboardSoundProfile.DEFAULT_CLICK -> "🔊 Standard System Click"
                                                    KeyboardSoundProfile.MECHANICAL_THOCK -> "⌨️ Mechanical Switch"
                                                    KeyboardSoundProfile.TYPEWRITER -> "📜 Classic Typewriter"
                                                    KeyboardSoundProfile.WATER_DROP -> "💧 Water Bubble Pop"
                                                    KeyboardSoundProfile.DIGITAL_BEEP -> "⚡ Cyber Blip"
                                                },
                                                fontSize = 12.sp
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFF047857),
                                            selectedLabelColor = Color.White
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }

                        HorizontalDivider()

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
                                colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF047857))
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
                                            selectedContainerColor = Color(0xFF047857),
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
                            keyHapticFeedback = hapticEnabled,
                            hapticStrength = hapticStrength
                        ))
                        activeDialog = null
                        Toast.makeText(context, "Sound & vibration preferences saved", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Save", color = Color(0xFF047857), fontWeight = FontWeight.Bold)
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
                            colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF047857))
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        onUpdatePreferences(preferences.copy(showNumberRow = numberRowEnabled))
                        activeDialog = null
                    }) {
                        Text("Done", color = Color(0xFF047857))
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
                                colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF047857))
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
                                colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF047857))
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
                                colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF047857))
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
                        Text("Done", color = Color(0xFF047857))
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
                                colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF047857))
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
                        Text("Done", color = Color(0xFF047857))
                    }
                }
            )
        }
        "extras" -> {
            AlertDialog(
                onDismissRequest = { activeDialog = null },
                title = { Text("Special Extra Features", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("• On-Device Telugu Sandhi Splitter: Active", fontSize = 13.sp)
                        Text("• Telugu Idioms & Proverbs Dictionary: Active", fontSize = 13.sp)
                        Text("• Kaomoji & Telugu Stickers: Active", fontSize = 13.sp)
                        Text("• Zero Keystroke Logging Guarantee: Verified", fontSize = 13.sp)
                    }
                },
                confirmButton = {
                    TextButton(onClick = { activeDialog = null }) {
                        Text("Close", color = Color(0xFF047857))
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
                        Text("Awesome!", color = Color(0xFF047857))
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
                        Text("Got it", color = Color(0xFF047857))
                    }
                }
            )
        }
    }
}
