package com.myplaces.app.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val EMOJI_LIST = listOf(
    // Emotions
    "😀", "😍", "😢", "😡", "😎", "🤔", "😴",
    // Nourriture
    "☕", "🍕", "🍷", "🍰", "🍣", "🍔", "🌮",
    // Nature
    "🌳", "🌊", "⛰️", "🌅", "🌸", "🌿", "🍀",
    // Activites
    "🏃", "📚", "🎵", "✈️", "🏠", "🎨", "⚽",
    // Lieux
    "🏛️", "🏖️", "🏔️", "🌆", "🏪", "🏥", "🎭"
)

@Composable
fun EmojiPicker(
    selectedEmoji: String,
    onEmojiSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 48.dp),
        modifier = modifier.heightIn(max = 200.dp),
        contentPadding = PaddingValues(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(EMOJI_LIST) { emoji ->
            val isSelected = emoji == selectedEmoji
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .border(
                        width = if (isSelected) 2.dp else 0.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onEmojiSelected(emoji) }
            ) {
                Text(
                    text = emoji,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
