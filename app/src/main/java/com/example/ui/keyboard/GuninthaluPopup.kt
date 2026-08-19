package com.example.ui.keyboard

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.TeluguScriptConstants

@Composable
fun GuninthaluPopup(
    modifier: Modifier = Modifier,
    initialConsonant: String = "క",
    palette: KeyboardPalette,
    onSelectChar: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var activeConsonant by remember(initialConsonant) {
        mutableStateOf(if (initialConsonant.isNotEmpty()) initialConsonant.take(1) else "క")
    }
    var selectedTab by remember { mutableIntStateOf(0) }

    val guninthaluList = remember(activeConsonant) { TeluguScriptConstants.getGuninthaluFor(activeConsonant) }
    val vottuluList = remember(activeConsonant) { TeluguScriptConstants.getVottuluFor(activeConsonant) }
    val matrasList = remember { TeluguScriptConstants.MATRAS.filter { it.isNotEmpty() } }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
            .shadow(16.dp, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        color = palette.surface,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header with Close & Active Consonant Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(palette.accent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = activeConsonant,
                            color = palette.accentText,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column {
                        Text(
                            text = "$activeConsonant - గుణింతాలు & వొత్తులు",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.keyText
                        )
                        Text(
                            text = "Tap any letter below to switch consonant instantly",
                            fontSize = 11.sp,
                            color = palette.keySecondaryText
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp).testTag("close_guninthalu_popup")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = palette.keySecondaryText
                    )
                }
            }

            // Quick Consonant Carousel (Allows picking ANY of the 36 Telugu Consonants)
            Text(
                text = "అక్షరం ఎంచుకోండి (Choose Consonant):",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.keySecondaryText
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(TeluguScriptConstants.CONSONANTS) { c ->
                    val isSelected = c == activeConsonant
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) palette.accent else palette.keyBackground)
                            .border(
                                width = if (isSelected) 1.5.dp else 0.5.dp,
                                color = if (isSelected) palette.accent else palette.border,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                activeConsonant = c
                            }
                            .testTag("select_consonant_$c"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = c,
                            color = if (isSelected) palette.accentText else palette.keyText,
                            fontSize = 15.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            // Tab Selector (Guninthalu vs Vottulu vs Matras)
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = palette.background,
                contentColor = palette.accent,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("గుణింతాలు (16)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("వొత్తులు (Conjuncts)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("మాత్రలు (Matras)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold) }
                )
            }

            // Grid items
            when (selectedTab) {
                0 -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(guninthaluList) { item ->
                            GuninthaluTile(
                                text = item,
                                palette = palette,
                                onClick = {
                                    onSelectChar(item)
                                    onDismiss()
                                }
                            )
                        }
                    }
                }
                1 -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(vottuluList) { item ->
                            GuninthaluTile(
                                text = item,
                                palette = palette,
                                onClick = {
                                    onSelectChar(item)
                                    onDismiss()
                                }
                            )
                        }
                    }
                }
                2 -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(matrasList) { matra ->
                            GuninthaluTile(
                                text = matra,
                                subtitle = activeConsonant + matra,
                                palette = palette,
                                onClick = {
                                    onSelectChar(matra)
                                    onDismiss()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GuninthaluTile(
    text: String,
    subtitle: String? = null,
    palette: KeyboardPalette,
    onClick: () -> Unit
) {
    val view = LocalView.current
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(palette.keyBackground)
            .border(0.5.dp, palette.border, RoundedCornerShape(10.dp))
            .clickable {
                try {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                } catch (_: Exception) {}
                onClick()
            }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = text,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = palette.keyText
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = palette.accent,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
