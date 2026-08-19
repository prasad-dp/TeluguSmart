package com.example.ui.keyboard

import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class EmojiCategory(
    val title: String,
    val icon: String,
    val emojis: List<String>
)

object EmojiData {
    val CATEGORIES = listOf(
        EmojiCategory(
            title = "భావాలు (Smiles & Fun)",
            icon = "😀",
            emojis = listOf(
                "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣",
                "😊", "😇", "🙂", "😉", "😌", "😍", "🥰", "😘",
                "😋", "😛", "😜", "🤪", "😝", "🤗", "🤩", "🥳",
                "😎", "🤓", "🧐", "🤔", "🤭", "🤫", "🤥", "🤤",
                "😬", "😮‍💨", "😴", "🥱", "🤮", "😷", "🤒", "🤕"
            )
        ),
        EmojiCategory(
            title = "గౌరవం & చేతులు (Respect & Hands)",
            icon = "🙏",
            emojis = listOf(
                "🙏", "🤝", "👏", "👍", "👎", "👊", "✊", "🤛",
                "🤜", "🤞", "✌️", "🤟", "🤘", "👌", "🤏", "👈",
                "👉", "👆", "👇", "☝️", "✋", "🤚", "🖐️", "🖖",
                "👋", "🤙", "💪", "🦾", "🤳", "🙌", "👐", "🤲"
            )
        ),
        EmojiCategory(
            title = "ప్రేమ & హృదయాలు (Love & Hearts)",
            icon = "❤️",
            emojis = listOf(
                "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍",
                "🤎", "💔", "❣️", "💕", "💞", "💓", "💗", "💖",
                "💘", "💝", "💟", "💌", "💐", "🌹", "🥀", "🌺"
            )
        ),
        EmojiCategory(
            title = "పండుగలు & సంబరాలు (Festivals)",
            icon = "🪔",
            emojis = listOf(
                "🪔", "🪁", "🌾", "🎂", "🎉", "🎊", "🎆", "🎇",
                "✨", "🎈", "🎁", "🏆", "🥇", "⭐", "🌟", "💫",
                "🚩", "🕉️", "🔔", "🥁", "🪕", "🔥", "🌈", "☀️"
            )
        ),
        EmojiCategory(
            title = "ఆహారం & స్వీట్స్ (Food & South)",
            icon = "🍛",
            emojis = listOf(
                "🍛", "🍚", "🍲", "☕", "🫖", "🥥", "🥭", "🍌",
                "🍉", "🍇", "🍓", "🍍", "🥥", "🍯", "🍬", "🍫",
                "🍿", "🍩", "🍪", "🎂", "🍰", "🧁", "🍦", "🍧"
            )
        )
    )
}

@Composable
fun EmojiPickerSheet(
    modifier: Modifier = Modifier,
    palette: KeyboardPalette,
    onSelectEmoji: (String) -> Unit,
    onClose: () -> Unit
) {
    var selectedCategoryIndex by remember { mutableStateOf(0) }
    val currentCategory = EmojiData.CATEGORIES.getOrElse(selectedCategoryIndex) { EmojiData.CATEGORIES[0] }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .background(palette.surface)
            .padding(8.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Telugu Emojis & Expressions (ఎమోజీలు)",
                color = palette.keyText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = onClose,
                modifier = Modifier.size(28.dp).testTag("close_emoji_sheet")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Emojis",
                    tint = palette.keySecondaryText
                )
            }
        }

        // Category Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            EmojiData.CATEGORIES.forEachIndexed { index, cat ->
                val isSelected = selectedCategoryIndex == index
                Surface(
                    onClick = { selectedCategoryIndex = index },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) palette.accent else palette.keyBackground,
                    modifier = Modifier.testTag("emoji_cat_$index")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = cat.icon, fontSize = 12.sp)
                        Text(
                            text = cat.title.substringBefore(" "),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) palette.accentText else palette.keyText
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Grid of Emojis
        LazyVerticalGrid(
            columns = GridCells.Fixed(8),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(currentCategory.emojis) { emoji ->
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onSelectEmoji(emoji) }
                        .padding(2.dp)
                        .testTag("emoji_$emoji"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 20.sp)
                }
            }
        }
    }
}
