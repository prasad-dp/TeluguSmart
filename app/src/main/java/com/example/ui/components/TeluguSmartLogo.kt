package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R

/**
 * Official TeluguSmart Logo Component
 * Renders the official 3D Telugu key emblem with brand-harmonized emerald/teal lighting.
 */
@Composable
fun TeluguSmartLogo(
    modifier: Modifier = Modifier,
    size: Dp = 160.dp,
    showText: Boolean = true,
    backgroundColor: Color = Color.Transparent,
    elevation: Dp = if (size > 60.dp) 8.dp else 2.dp,
    border: BorderStroke? = null
) {
    val cornerRadius = size * 0.22f
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .size(size)
            .then(if (elevation > 0.dp) Modifier.shadow(elevation, shape, clip = false) else Modifier)
            .clip(shape)
            .background(backgroundColor)
            .then(if (border != null) Modifier.border(border, shape) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = "TeluguSmart Official Keyboard Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}
