package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Pixel-perfect Mini Key for Keyboard Mockup Cards
 */
@Composable
fun MiniMockupKey(
    text: String,
    subscript: String? = null,
    modifier: Modifier = Modifier,
    isDarkKey: Boolean = false,
    textColor: Color = if (isDarkKey) Color(0xFF334155) else Color(0xFF1E293B),
    fontSize: Float = 6.5f
) {
    val keyBg = if (isDarkKey) Color(0xFFD6DBE4) else Color(0xFFFFFFFF)
    Box(
        modifier = modifier
            .height(13.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(keyBg)
            .border(0.3.dp, Color(0xFFCBD5E1), RoundedCornerShape(2.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (subscript != null) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = subscript,
                    fontSize = 3.8.sp,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 1.dp, top = 0.5.dp)
                )
                Text(
                    text = text,
                    fontSize = fontSize.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            Text(
                text = text,
                fontSize = fontSize.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * 1. Transliteration (abc -> తెలుగు) Mockup
 */
@Composable
fun TranslitMockup(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFECEFF3))
            .padding(horizontal = 4.dp, vertical = 3.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Suggestion strip: :: అలాగే ఆలాగే అలగే 🎙
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(13.dp)
                .background(Color(0xFFE2E8F0), RoundedCornerShape(2.dp))
                .padding(horizontal = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("::", fontSize = 7.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("అలాగే", fontSize = 6.5.sp, color = Color(0xFF1E293B), fontWeight = FontWeight.Bold)
                Text("ఆలాగే", fontSize = 6.sp, color = Color(0xFF64748B))
                Text("అలగే", fontSize = 6.sp, color = Color(0xFF64748B))
            }
            Icon(Icons.Default.Mic, contentDescription = null, tint = Color(0xFF475569), modifier = Modifier.size(8.dp))
        }

        // QWERTY Rows
        Column(verticalArrangement = Arrangement.spacedBy(1.5.dp)) {
            // Row 1
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                listOf(
                    "Q" to "1", "W" to "2", "E" to "3", "R" to "4", "T" to "5",
                    "Y" to "6", "U" to "7", "I" to "8", "O" to "9", "P" to "0"
                ).forEach { (k, sub) ->
                    MiniMockupKey(text = k, subscript = sub, modifier = Modifier.weight(1f))
                }
            }
            // Row 2
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                listOf("A", "S", "D", "F", "G", "H", "J", "K", "L").forEach { k ->
                    MiniMockupKey(text = k, modifier = Modifier.weight(1f))
                }
            }
            // Row 3
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                MiniMockupKey(text = "⇧", modifier = Modifier.weight(1.3f), isDarkKey = true, fontSize = 7f)
                listOf("Z", "X", "C", "V", "B", "N", "M").forEach { k ->
                    MiniMockupKey(text = k, modifier = Modifier.weight(1f))
                }
                MiniMockupKey(text = "⌫", modifier = Modifier.weight(1.3f), isDarkKey = true, fontSize = 7f)
            }
            // Row 4
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                MiniMockupKey(text = "?123", modifier = Modifier.weight(1.2f), isDarkKey = true, fontSize = 5.5f)
                MiniMockupKey(text = ",", modifier = Modifier.weight(0.8f), isDarkKey = true)
                MiniMockupKey(text = "తె", modifier = Modifier.weight(1f), isDarkKey = false, fontSize = 6.5f)
                Box(
                    modifier = Modifier
                        .weight(3.5f)
                        .height(13.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White)
                        .border(0.3.dp, Color(0xFFCBD5E1), RoundedCornerShape(2.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Telugu Smart Keyboard", fontSize = 4.2.sp, color = Color(0xFF94A3B8))
                }
                MiniMockupKey(text = ".", modifier = Modifier.weight(0.8f), isDarkKey = true)
                MiniMockupKey(text = "↵", modifier = Modifier.weight(1.2f), isDarkKey = true, fontSize = 6.5f)
            }
        }
    }
}

/**
 * 2. English Keyboard Mockup
 */
@Composable
fun EnglishMockup(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFECEFF3))
            .padding(horizontal = 4.dp, vertical = 3.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Suggestion strip: :: Beau Beautiful Beauty 🎙
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(13.dp)
                .background(Color(0xFFE2E8F0), RoundedCornerShape(2.dp))
                .padding(horizontal = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("::", fontSize = 7.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Beau", fontSize = 6.5.sp, color = Color(0xFF1E293B), fontWeight = FontWeight.Bold)
                Text("Beautiful", fontSize = 6.sp, color = Color(0xFF64748B))
                Text("Beauty", fontSize = 6.sp, color = Color(0xFF64748B))
            }
            Icon(Icons.Default.Mic, contentDescription = null, tint = Color(0xFF475569), modifier = Modifier.size(8.dp))
        }

        // QWERTY Rows
        Column(verticalArrangement = Arrangement.spacedBy(1.5.dp)) {
            // Row 1
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                listOf(
                    "Q" to "1", "W" to "2", "E" to "3", "R" to "4", "T" to "5",
                    "Y" to "6", "U" to "7", "I" to "8", "O" to "9", "P" to "0"
                ).forEach { (k, sub) ->
                    MiniMockupKey(text = k, subscript = sub, modifier = Modifier.weight(1f))
                }
            }
            // Row 2
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                listOf("A", "S", "D", "F", "G", "H", "J", "K", "L").forEach { k ->
                    MiniMockupKey(text = k, modifier = Modifier.weight(1f))
                }
            }
            // Row 3
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                MiniMockupKey(text = "⇧", modifier = Modifier.weight(1.3f), isDarkKey = true, fontSize = 7f)
                listOf("Z", "X", "C", "V", "B", "N", "M").forEach { k ->
                    MiniMockupKey(text = k, modifier = Modifier.weight(1f))
                }
                MiniMockupKey(text = "⌫", modifier = Modifier.weight(1.3f), isDarkKey = true, fontSize = 7f)
            }
            // Row 4
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                MiniMockupKey(text = "?123", modifier = Modifier.weight(1.2f), isDarkKey = true, fontSize = 5.5f)
                MiniMockupKey(text = ",", modifier = Modifier.weight(0.8f), isDarkKey = true)
                MiniMockupKey(text = "తె", modifier = Modifier.weight(1f), isDarkKey = false, fontSize = 6.5f)
                Box(
                    modifier = Modifier
                        .weight(3.5f)
                        .height(13.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White)
                        .border(0.3.dp, Color(0xFFCBD5E1), RoundedCornerShape(2.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Telugu Smart Keyboard", fontSize = 4.2.sp, color = Color(0xFF94A3B8))
                }
                MiniMockupKey(text = ".", modifier = Modifier.weight(0.8f), isDarkKey = true)
                MiniMockupKey(text = "↵", modifier = Modifier.weight(1.2f), isDarkKey = true, fontSize = 6.5f)
            }
        }
    }
}

/**
 * 3. Handwriting Canvas Mockup
 */
@Composable
fun HandwritingMockup(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFECEFF3))
            .padding(horizontal = 4.dp, vertical = 3.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Suggestion strip: :: అలాగే alage 🎙
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(13.dp)
                .background(Color(0xFFE2E8F0), RoundedCornerShape(2.dp))
                .padding(horizontal = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("::", fontSize = 7.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("అలాగే", fontSize = 6.5.sp, color = Color(0xFF1E293B), fontWeight = FontWeight.Bold)
                Text("alage", fontSize = 6.sp, color = Color(0xFF64748B))
            }
            Icon(Icons.Default.Mic, contentDescription = null, tint = Color(0xFF475569), modifier = Modifier.size(8.dp))
        }

        // Handwriting Drawing Canvas with Stylized Telugu Character and Finger prompt
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 2.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFE2E8F0)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Drawing glyph illustration
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "అ",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF38BDF8))
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Write here using your finger",
                    fontSize = 6.5.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )
            }

            // Right-aligned backspace key
            MiniMockupKey(
                text = "⌫",
                isDarkKey = true,
                fontSize = 7f,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 4.dp, bottom = 4.dp)
                    .size(width = 16.dp, height = 14.dp)
            )
        }

        // Bottom Action Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            MiniMockupKey(text = "?123", modifier = Modifier.weight(1.2f), isDarkKey = true, fontSize = 5.5f)
            MiniMockupKey(text = ",", modifier = Modifier.weight(0.8f), isDarkKey = true)
            MiniMockupKey(text = "AB CD", modifier = Modifier.weight(1.2f), isDarkKey = false, fontSize = 4.5f)
            Box(
                modifier = Modifier
                    .weight(3.2f)
                    .height(13.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White)
                    .border(0.3.dp, Color(0xFFCBD5E1), RoundedCornerShape(2.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("Telugu Smart Keyboard", fontSize = 4.2.sp, color = Color(0xFF94A3B8))
            }
            MiniMockupKey(text = ".", modifier = Modifier.weight(0.8f), isDarkKey = true)
            MiniMockupKey(text = "↵", modifier = Modifier.weight(1.2f), isDarkKey = true, fontSize = 6.5f)
        }
    }
}

/**
 * 4. Varnamala (Exact 6-Row Native Telugu Matrix) Mockup
 */
@Composable
fun VarnamalaMockup(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFECEFF3))
            .padding(horizontal = 3.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Suggestion strip: :: అ అలాగే ఆలాగే అలగే 🎙
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .background(Color(0xFFE2E8F0), RoundedCornerShape(2.dp))
                .padding(horizontal = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("::", fontSize = 6.5.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
            Text("అ", fontSize = 6.5.sp, color = Color(0xFF1E293B), fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                Text("అలాగే", fontSize = 5.5.sp, color = Color(0xFF475569))
                Text("ఆలాగే", fontSize = 5.5.sp, color = Color(0xFF64748B))
                Text("అలగే", fontSize = 5.5.sp, color = Color(0xFF64748B))
            }
            Icon(Icons.Default.Mic, contentDescription = null, tint = Color(0xFF475569), modifier = Modifier.size(7.5.dp))
        }

        // Exact 6 Telugu Rows
        Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
            // Row 1 (10 Vowels - Grey keys)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(0.8.dp)) {
                listOf("అ", "ఆ", "ఇ", "ఈ", "ఉ", "ఊ", "ఋ", "ౠ", "ఎ", "ఏ").forEach { k ->
                    MiniMockupKey(text = k, isDarkKey = true, fontSize = 5.5f, modifier = Modifier.weight(1f))
                }
            }
            // Row 2 (6 Vowels + 4 Consonants)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(0.8.dp)) {
                listOf(
                    "ఐ" to true, "ఒ" to true, "ఓ" to true, "ఔ" to true, "ం" to true, "ః" to true,
                    "క" to false, "ఖ" to false, "గ" to false, "ఘ" to false
                ).forEach { (k, isDark) ->
                    MiniMockupKey(text = k, isDarkKey = isDark, fontSize = 5.5f, modifier = Modifier.weight(1f))
                }
            }
            // Row 3 (1 Halant + 9 Consonants)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(0.8.dp)) {
                listOf(
                    "్" to true, "చ" to false, "ఛ" to false, "జ" to false, "ఝ" to false,
                    "ట" to false, "ఠ" to false, "డ" to false, "ఢ" to false, "ణ" to false
                ).forEach { (k, isDark) ->
                    MiniMockupKey(text = k, isDarkKey = isDark, fontSize = 5.5f, modifier = Modifier.weight(1f))
                }
            }
            // Row 4 (10 Consonants)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(0.8.dp)) {
                listOf("త", "థ", "ద", "ధ", "న", "ప", "ఫ", "బ", "భ", "మ").forEach { k ->
                    MiniMockupKey(text = k, isDarkKey = false, fontSize = 5.5f, modifier = Modifier.weight(1f))
                }
            }
            // Row 5 (9 Consonants + Backspace)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(0.8.dp)) {
                listOf("య", "ర", "ల", "వ", "ళ", "శ", "ష", "స", "హ").forEach { k ->
                    MiniMockupKey(text = k, isDarkKey = false, fontSize = 5.5f, modifier = Modifier.weight(1f))
                }
                MiniMockupKey(text = "⌫", isDarkKey = true, fontSize = 5.5f, modifier = Modifier.weight(1f))
            }
            // Row 6 (Bottom Action Row)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(0.8.dp)) {
                MiniMockupKey(text = "?123", modifier = Modifier.weight(1.1f), isDarkKey = true, fontSize = 4.5f)
                MiniMockupKey(text = "౹", modifier = Modifier.weight(0.7f), isDarkKey = true, fontSize = 5f)
                MiniMockupKey(text = "AB CD", modifier = Modifier.weight(1.1f), isDarkKey = false, fontSize = 4.2f)
                Box(
                    modifier = Modifier
                        .weight(3.5f)
                        .height(13.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White)
                        .border(0.3.dp, Color(0xFFCBD5E1), RoundedCornerShape(2.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Telugu Smart Keyboard", fontSize = 4.sp, color = Color(0xFF94A3B8))
                }
                MiniMockupKey(text = ".", modifier = Modifier.weight(0.7f), isDarkKey = true)
                MiniMockupKey(text = "↵", modifier = Modifier.weight(1.1f), isDarkKey = true, fontSize = 6f)
            }
        }
    }
}

/**
 * 5. Voice Typing Mockup
 */
@Composable
fun VoiceMockup(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFECEFF3))
            .padding(horizontal = 4.dp, vertical = 3.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: ← English (Active pill) | తెలుగు | Telugu (en) | 🎙
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(15.dp)
                    .background(Color(0xFFE2E8F0), RoundedCornerShape(2.dp))
                    .padding(horizontal = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = Color(0xFF475569),
                    modifier = Modifier.size(8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    // Active English Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White)
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text("English", fontSize = 5.sp, color = Color(0xFF1E293B), fontWeight = FontWeight.Bold)
                    }
                    Text("తెలుగు", fontSize = 5.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
                    Text("Telugu (en)", fontSize = 5.sp, color = Color(0xFF64748B))
                }

                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = null,
                    tint = Color(0xFF475569),
                    modifier = Modifier.size(8.dp)
                )
            }

            // Faint Keyboard Rows in Background
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                    listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P").forEach { k ->
                        MiniMockupKey(text = k, isDarkKey = false, textColor = Color(0xFFCBD5E1), modifier = Modifier.weight(1f))
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    listOf("A", "S", "D", "F", "G", "H", "J", "K", "L").forEach { k ->
                        MiniMockupKey(text = k, isDarkKey = false, textColor = Color(0xFFCBD5E1), modifier = Modifier.weight(1f))
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                    listOf("Z", "X", "C", "V", "B", "N", "M").forEach { k ->
                        MiniMockupKey(text = k, isDarkKey = false, textColor = Color(0xFFCBD5E1), modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Centered Big Circular Microphone Button Badge (as in screenshot)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(38.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.2.dp, Color(0xFF1E293B), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Voice Microphone",
                tint = Color(0xFF0F172A),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
