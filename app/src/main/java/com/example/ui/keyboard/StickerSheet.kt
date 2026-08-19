package com.example.ui.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.StickerData
import com.example.model.TeluguMemeSticker

@Composable
fun StickerSheet(
    modifier: Modifier = Modifier,
    palette: KeyboardPalette,
    onSelectSticker: (TeluguMemeSticker) -> Unit,
    onOpenCustomMaker: () -> Unit = {},
    onClose: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(270.dp)
            .background(palette.surface)
            .padding(10.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Telugu Meme Stickers (తెలుగు మీమ్స్)",
                    color = palette.keyText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "12 Classic Telugu punchlines & custom maker",
                    color = palette.keySecondaryText,
                    fontSize = 10.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                androidx.compose.material3.Surface(
                    onClick = onOpenCustomMaker,
                    shape = RoundedCornerShape(8.dp),
                    color = palette.accent,
                    modifier = Modifier.testTag("open_custom_maker_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = palette.accentText,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "+ Create",
                            color = palette.accentText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(28.dp).testTag("close_sticker_sheet")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Stickers",
                        tint = palette.keySecondaryText
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(StickerData.BUNDLED_STICKERS, key = { it.id }) { sticker ->
                StickerCard(
                    sticker = sticker,
                    palette = palette,
                    onClick = { onSelectSticker(sticker) }
                )
            }
        }
    }
}

@Composable
private fun StickerCard(
    sticker: TeluguMemeSticker,
    palette: KeyboardPalette,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(sticker.bgColorHex))
            .border(1.dp, Color(sticker.fgColorHex).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
            .testTag("sticker_card_${sticker.id}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = sticker.emoji,
                fontSize = 24.sp
            )
            Text(
                text = sticker.punchlineTelugu,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(sticker.fgColorHex),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = sticker.characterName,
                fontSize = 9.sp,
                color = Color(sticker.fgColorHex).copy(alpha = 0.8f)
            )
        }
    }
}
