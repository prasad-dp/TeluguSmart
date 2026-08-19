package com.example.ui.keyboard

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GifData
import com.example.model.TeluguGifItem

@Composable
fun GifPickerSheet(
    modifier: Modifier = Modifier,
    palette: KeyboardPalette,
    onSelectGif: (TeluguGifItem) -> Unit,
    onClose: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = remember {
        listOf("All", "Comedy", "Mass", "Dance", "Swag", "Epic")
    }

    val filteredGifs = remember(searchQuery, selectedCategory) {
        GifData.BUNDLED_GIFS.filter { gif ->
            val matchCategory = selectedCategory == "All" || gif.category.equals(selectedCategory, ignoreCase = true)
            val matchSearch = searchQuery.isBlank() ||
                gif.title.contains(searchQuery, ignoreCase = true) ||
                gif.actor.contains(searchQuery, ignoreCase = true) ||
                gif.captionTelugu.contains(searchQuery, ignoreCase = true)
            matchCategory && matchSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(palette.surface)
            .padding(8.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Telugu Animated GIFs (తెలుగు జిఫ్స్)",
                    color = palette.keyText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Trending Tollywood animated punchlines & reactions",
                    color = palette.keySecondaryText,
                    fontSize = 10.sp
                )
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier.size(28.dp).testTag("close_gif_sheet")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close GIFs",
                    tint = palette.keySecondaryText
                )
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search Tollywood GIFs (Brahmi, Pushpa, Balayya)...", fontSize = 11.sp, color = palette.keySecondaryText) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = palette.keySecondaryText, modifier = Modifier.size(16.dp))
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .padding(vertical = 2.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = palette.accent,
                unfocusedBorderColor = palette.border,
                focusedTextColor = palette.keyText,
                unfocusedTextColor = palette.keyText,
                focusedContainerColor = palette.background,
                unfocusedContainerColor = palette.background
            ),
            shape = RoundedCornerShape(8.dp)
        )

        // Filter Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            categories.forEach { category ->
                val isSelected = selectedCategory == category
                Surface(
                    onClick = { selectedCategory = category },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) palette.accent else palette.keyBackground,
                    modifier = Modifier.testTag("gif_cat_$category")
                ) {
                    Text(
                        text = category,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) palette.accentText else palette.keyText,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Grid of GIFs
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(filteredGifs, key = { it.id }) { gif ->
                AnimatedGifCard(
                    gif = gif,
                    palette = palette,
                    onClick = { onSelectGif(gif) }
                )
            }
        }
    }
}

@Composable
private fun AnimatedGifCard(
    gif: TeluguGifItem,
    palette: KeyboardPalette,
    onClick: () -> Unit
) {
    // Cyclic animation for GIF preview effect
    val transition = rememberInfiniteTransition(label = "gif_anim")
    val animProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = gif.animationFrames.size.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "frame_index"
    )

    val currentFrameIndex = animProgress.toInt().coerceIn(0, gif.animationFrames.size - 1)
    val currentEmoji = gif.animationFrames.getOrElse(currentFrameIndex) { gif.emoji }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(palette.keyBackground)
            .border(1.dp, Color(gif.accentColorHex).copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(6.dp)
            .testTag("gif_card_${gif.id}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // GIF Visual Frame with animated pulse
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(gif.accentColorHex).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentEmoji,
                    fontSize = 24.sp,
                    modifier = Modifier.graphicsLayer {
                        scaleX = 1f + (currentFrameIndex % 2) * 0.1f
                        scaleY = 1f + (currentFrameIndex % 2) * 0.1f
                    }
                )
            }

            Text(
                text = gif.captionTelugu,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = palette.keyText,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = gif.actor,
                    fontSize = 8.5.sp,
                    color = Color(gif.accentColorHex),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "GIF",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.accentText,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(palette.accent)
                        .padding(horizontal = 3.dp, vertical = 1.dp)
                )
            }
        }
    }
}
