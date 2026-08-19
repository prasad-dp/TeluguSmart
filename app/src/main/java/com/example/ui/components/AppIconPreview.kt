package com.example.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * High-fidelity vector preview of the Telugu Keyboard App Icon.
 * Replicates the exact visual geometry of the official logo and adaptive launcher icon.
 */
@Composable
fun TeluguAppIconView(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(size * 0.22f),
    elevation: Dp = 4.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(elevation, shape)
            .clip(shape),
        contentAlignment = Alignment.Center
    ) {
        TeluguSmartLogo(
            modifier = Modifier.size(size),
            size = size,
            showText = false
        )
    }
}
