package com.example.ui.keyboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.SpaceBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.KeyboardPreferences

data class StrokeLine(val points: List<Offset>)

@Composable
fun HandwritingPadSheet(
    palette: KeyboardPalette,
    preferences: KeyboardPreferences,
    onInsertChar: (String) -> Unit,
    onBackspace: () -> Unit,
    onSpace: () -> Unit,
    onEnter: () -> Unit,
    onSwitchToKeyboard: () -> Unit
) {
    val strokes = remember { mutableStateListOf<StrokeLine>() }
    var currentPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }

    val candidates = listOf("అ", "ఆ", "ఇ", "ఈ", "ఉ", "క", "గ", "చ", "జ", "త", "ద", "న", "ప", "బ", "మ", "య", "ర", "ల", "వ", "స", "హ")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(palette.background)
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag("handwriting_pad_sheet")
    ) {
        // Top candidate strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .background(palette.surface, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "గుర్తించినవి:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = palette.keySecondaryText
            )
            Spacer(modifier = Modifier.width(6.dp))

            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(candidates) { char ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = palette.keyBackground,
                        border = androidx.compose.foundation.BorderStroke(1.dp, palette.border.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .size(width = 38.dp, height = 32.dp)
                            .clickable {
                                onInsertChar(char)
                                strokes.clear()
                                currentPoints = emptyList()
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = char,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.keyText
                            )
                        }
                    }
                }
            }

            // Clear strokes button
            IconButton(
                onClick = {
                    strokes.clear()
                    currentPoints = emptyList()
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear Pad",
                    tint = palette.keySecondaryText,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Drawing Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(palette.surface)
                .border(1.dp, palette.border.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            currentPoints = listOf(offset)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            currentPoints = currentPoints + change.position
                        },
                        onDragEnd = {
                            if (currentPoints.isNotEmpty()) {
                                strokes.add(StrokeLine(currentPoints))
                                currentPoints = emptyList()
                            }
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Completed strokes
                strokes.forEach { stroke ->
                    if (stroke.points.size > 1) {
                        val path = Path()
                        path.moveTo(stroke.points.first().x, stroke.points.first().y)
                        for (i in 1 until stroke.points.size) {
                            path.lineTo(stroke.points[i].x, stroke.points[i].y)
                        }
                        drawPath(
                            path = path,
                            color = palette.accent,
                            style = Stroke(
                                width = 8f,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }

                // Currently active stroke
                if (currentPoints.size > 1) {
                    val path = Path()
                    path.moveTo(currentPoints.first().x, currentPoints.first().y)
                    for (i in 1 until currentPoints.size) {
                        path.lineTo(currentPoints[i].x, currentPoints[i].y)
                    }
                    drawPath(
                        path = path,
                        color = palette.accent,
                        style = Stroke(
                            width = 8f,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }

            if (strokes.isEmpty() && currentPoints.isEmpty()) {
                Text(
                    text = "ఇక్కడ మీ చేతితో తెలుగు అక్షరాలను వ్రాయండి\n(Draw Telugu letters here)",
                    fontSize = 13.sp,
                    color = palette.keySecondaryText.copy(alpha = 0.6f),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Bottom Action Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Switch back to Keyboard
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = palette.keyBackground,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clickable(onClick = onSwitchToKeyboard)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Keyboard, contentDescription = "Keyboard", tint = palette.keyText, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("కీబోర్డ్", fontSize = 13.sp, color = palette.keyText, fontWeight = FontWeight.Bold)
                }
            }

            // Space key
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = palette.keyBackground,
                modifier = Modifier
                    .weight(1.8f)
                    .height(44.dp)
                    .clickable(onClick = onSpace)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.SpaceBar, contentDescription = "Space", tint = palette.keyText, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("స్పేస్", fontSize = 13.sp, color = palette.keyText)
                }
            }

            // Backspace key
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = palette.keyBackground,
                modifier = Modifier
                    .weight(0.9f)
                    .height(44.dp)
                    .clickable(onClick = onBackspace)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Backspace, contentDescription = "Backspace", tint = palette.keyText, modifier = Modifier.size(18.dp))
                }
            }

            // Enter key
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = palette.accent,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clickable(onClick = onEnter)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("↵", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
