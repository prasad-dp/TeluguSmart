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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.KeyboardPreferences
import com.example.engine.TeluguTransliterationEngine

data class LayoutOption(
    val id: String,
    val title: String,
    val subtitle: String,
    val badge: String,
    val previewType: String,
    val sampleKeys: List<String>
)

data class SpecialFeature(
    val title: String,
    val desc: String,
    val icon: ImageVector,
    val accentColor: Color,
    val tag: String
)

@Composable
fun HomeScreen(
    preferences: KeyboardPreferences,
    onUpdatePreferences: (KeyboardPreferences) -> Unit,
    isDarkMode: Boolean
) {
    val context = LocalContext.current
    var selectedLayoutId by remember { mutableStateOf("telugu_translit") }
    var sandboxInput by remember { mutableStateOf("") }
    var sandboxOutput by remember { mutableStateOf("") }

    val layouts = listOf(
        LayoutOption(
            id = "telugu_translit",
            title = "abc → తెలుగు",
            subtitle = "Phonetic Smart Typing",
            badge = "Recommended",
            previewType = "translit",
            sampleKeys = listOf("అలాగే", "నమస్కారం", "ఎలా ఉన్నారు")
        ),
        LayoutOption(
            id = "english",
            title = "English",
            subtitle = "Standard QWERTY",
            badge = "Default",
            previewType = "english",
            sampleKeys = listOf("Hello", "Beautiful", "Smart")
        ),
        LayoutOption(
            id = "handwriting",
            title = "Handwriting",
            subtitle = "వ్రాత • Finger Drawing",
            badge = "Touch Draw",
            previewType = "draw",
            sampleKeys = listOf("ఆ", "తెలుగు", "కలం")
        ),
        LayoutOption(
            id = "varnamala",
            title = "వర్ణమాల",
            subtitle = "Direct Alphabet Matrix",
            badge = "Varnamala",
            previewType = "varnamala",
            sampleKeys = listOf("అ", "ఆ", "ఇ", "ఈ", "క", "ఖ")
        ),
        LayoutOption(
            id = "voice",
            title = "Voice typing",
            subtitle = "వాయిస్ టైపింగ్ • Speech to Text",
            badge = "Audio",
            previewType = "voice",
            sampleKeys = listOf("మైక్ నొక్కి మాట్లాడండి")
        )
    )

    val standoutFeatures = listOf(
        SpecialFeature(
            title = "Zero-Latency Offline Engine",
            desc = "100% On-device processing. Blazing-fast typing with zero internet or cloud latency.",
            icon = Icons.Default.Speed,
            accentColor = Color(0xFF059669),
            tag = "0ms Lag"
        ),
        SpecialFeature(
            title = "Privacy Shield Guarantee",
            desc = "No keystroke logging, no telemetry, no tracking. Your private chats stay on your phone.",
            icon = Icons.Default.Security,
            accentColor = Color(0xFF0284C7),
            tag = "100% Safe"
        ),
        SpecialFeature(
            title = "AI Smart Transliteration",
            desc = "Accurately resolves complex Telugu Guninthalu (గుణింతాలు), Vattulu (వత్తులు) & Sandhi.",
            icon = Icons.Default.Psychology,
            accentColor = Color(0xFF7C3AED),
            tag = "Smart AI"
        ),
        SpecialFeature(
            title = "Slang & Proverbs Dictionary",
            desc = "Instant suggestions for Telangana, Rayalaseema, and Andhra proverbs & daily idioms.",
            icon = Icons.Default.Spellcheck,
            accentColor = Color(0xFFD97706),
            tag = "సామెతలు"
        ),
        SpecialFeature(
            title = "Sandhi Splitter & Joiner",
            desc = "One-tap Telugu grammatical Sandhi analysis for literature, essays, and academic writing.",
            icon = Icons.Default.AutoAwesome,
            accentColor = Color(0xFFDB2777),
            tag = "సంధి"
        )
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 80.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .testTag("layouts_screen_grid")
    ) {
        // Section 1: Top Welcome Header & Active Layout Indicator
        item(span = { GridItemSpan(2) }) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkMode) Color(0xFF161F30) else Color(0xFFF0FDF4)
                ),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDarkMode) Color(0xFF1E293B) else Color(0xFFBBF7D0)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Selected Layout: ",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = layouts.find { it.id == selectedLayoutId }?.title ?: "abc → తెలుగు",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF059669)
                            )
                        }
                        Text(
                            text = "Tap any card below to activate and customize your keyboard layout.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF059669)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Keyboard,
                            contentDescription = "Keyboard",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Section 2: Layout Cards (Matching Screenshot 5)
        items(layouts) { layout ->
            val isSelected = selectedLayoutId == layout.id
            LayoutPreviewCard(
                layout = layout,
                isSelected = isSelected,
                isDarkMode = isDarkMode,
                onClick = {
                    selectedLayoutId = layout.id
                    Toast.makeText(context, "Layout switched to: ${layout.title}", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // Section 3: Industry-Leading Standout Features
        item(span = { GridItemSpan(2) }) {
            Column(modifier = Modifier.padding(top = 16.dp, bottom = 6.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Special Features",
                        tint = Color(0xFF059669),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Why We Stand Out",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    text = "Engineered with proprietary on-device Telugu NLP algorithms",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(standoutFeatures) { feature ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(feature.accentColor.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = feature.icon,
                                contentDescription = feature.title,
                                tint = feature.accentColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = feature.accentColor.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = feature.tag,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = feature.accentColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = feature.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = feature.desc,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Section 4: Live Interactive Typing Playground Card
        item(span = { GridItemSpan(2) }) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDarkMode) Color(0xFF334155) else Color(0xFFE2E8F0)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Live Transliteration Test Sandbox",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Type phonetic English (e.g. namaste, telugu) to see instant conversion:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = sandboxInput,
                        onValueChange = { input ->
                            sandboxInput = input
                            sandboxOutput = TeluguTransliterationEngine.convertPhonetic(input)
                        },
                        placeholder = { Text("Type in English (e.g. namaste)", fontSize = 14.sp) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_sandbox_textfield"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF059669),
                            unfocusedBorderColor = if (isDarkMode) Color(0xFF334155) else Color(0xFFCBD5E1)
                        )
                    )

                    if (sandboxOutput.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF059669).copy(alpha = 0.1f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Telugu Output: $sandboxOutput",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF047857),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LayoutPreviewCard(
    layout: LayoutOption,
    isSelected: Boolean,
    isDarkMode: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) Color(0xFF161F30) else Color(0xFFFFFFFF)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) Color(0xFF059669) else if (isDarkMode) Color(0xFF334155) else Color(0xFFE5E7EB)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("layout_card_${layout.id}")
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // Simulated Keyboard Key Preview Box (Matching Screenshot 5)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0F172A)),
                contentAlignment = Alignment.Center
            ) {
                when (layout.previewType) {
                    "translit" -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(6.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Top suggestion strip
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF1E293B), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                layout.sampleKeys.forEach { word ->
                                    Text(word, color = Color(0xFF34D399), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            // Keys grid simulation
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    listOf("Q","W","E","R","T","Y","U","I","O","P").forEach { k ->
                                        MiniKey(k)
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    listOf("A","S","D","F","G","H","J","K","L").forEach { k ->
                                        MiniKey(k)
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    listOf("Z","X","C","V","B","N","M").forEach { k ->
                                        MiniKey(k)
                                    }
                                }
                            }
                        }
                    }
                    "english" -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(6.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF1E293B), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                layout.sampleKeys.forEach { word ->
                                    Text(word, color = Color.White, fontSize = 9.sp)
                                }
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    listOf("Q","W","E","R","T","Y","U","I","O","P").forEach { k ->
                                        MiniKey(k)
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    listOf("A","S","D","F","G","H","J","K","L").forEach { k ->
                                        MiniKey(k)
                                    }
                                }
                            }
                        }
                    }
                    "draw" -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Create,
                                contentDescription = "Draw",
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "వ్రాత పలక",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Write with finger",
                                color = Color(0xFF94A3B8),
                                fontSize = 9.sp
                            )
                        }
                    }
                    "varnamala" -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(6.dp),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                listOf("అ","ఆ","ఇ","ఈ","ఉ","ఊ").forEach { k -> MiniKey(k, isTelugu = true) }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                listOf("క","ఖ","గ","ఘ","ఙ").forEach { k -> MiniKey(k, isTelugu = true) }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                listOf("చ","ఛ","జ","ఝ","ఞ").forEach { k -> MiniKey(k, isTelugu = true) }
                            }
                        }
                    }
                    "voice" -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF059669)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Voice",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "వాయిస్ టైపింగ్",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Checkmark badge in corner if selected
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF059669)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Active",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Label Pill (Matching Screenshot 5)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) Color(0xFF047857) else if (isDarkMode) Color(0xFF1E293B) else Color(0xFFF3F4F6),
                border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, if (isDarkMode) Color(0xFF334155) else Color(0xFFE5E7EB)) else null
            ) {
                Text(
                    text = layout.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun MiniKey(label: String, isTelugu: Boolean = false) {
    Box(
        modifier = Modifier
            .size(width = if (isTelugu) 20.dp else 11.dp, height = 14.dp)
            .background(Color(0xFF334155), RoundedCornerShape(2.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = if (isTelugu) 8.sp else 7.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
