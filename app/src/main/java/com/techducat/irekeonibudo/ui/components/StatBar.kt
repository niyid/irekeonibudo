package com.techducat.irekeonibudo.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.techducat.irekeonibudo.ui.theme.BoneWhite

/**
 * A hand-drawn stat bar: rounded glass track, gradient fill, a soft glow at
 * the fill's leading edge, and a subtle bright "meniscus" line at the top —
 * a more atmospheric alternative to the stock Material progress indicator.
 */
@Composable
fun StatBar(label: String, value: Int, max: Int = 100, color: Color, modifier: Modifier = Modifier) {
    val fraction by animateFloatAsState(
        targetValue = (value.toFloat() / max).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 420),
        label = "statBarFraction"
    )
    Column(modifier = modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = BoneWhite.copy(alpha = 0.85f)
            )
            Text(
                text = "$value/$max",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = color
            )
        }
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
        ) {
            val h = size.height
            val w = size.width
            val radius = CornerRadius(h / 2f)
            val trackRect = RoundRect(0f, 0f, w, h, radius)
            val trackPath = Path().apply { addRoundRect(trackRect) }

            // Recessed track — a soft inner-shadow look via a darker fill plus a thin rim.
            drawPath(trackPath, color = Color.Black.copy(alpha = 0.35f))
            drawPath(trackPath, color = color.copy(alpha = 0.16f))

            val fillWidth = (w * fraction).coerceAtLeast(if (fraction > 0f) h else 0f)
            if (fillWidth > 0f) {
                val fillRect = RoundRect(0f, 0f, fillWidth, h, radius)
                val fillPath = Path().apply { addRoundRect(fillRect) }
                clipPath(fillPath) {
                    drawRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(color.copy(alpha = 0.55f), color),
                            startX = 0f,
                            endX = fillWidth
                        ),
                        size = Size(fillWidth, h)
                    )
                    // Bright meniscus along the very top of the fill.
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(BoneWhite.copy(alpha = 0.45f), Color.Transparent),
                            startY = 0f,
                            endY = h * 0.6f
                        ),
                        size = Size(fillWidth, h * 0.6f)
                    )
                }
                // A soft glow bleeding past the leading edge of the fill.
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(color.copy(alpha = 0.55f), Color.Transparent),
                        center = Offset(fillWidth, h / 2f),
                        radius = h * 1.6f
                    ),
                    radius = h * 1.6f,
                    center = Offset(fillWidth, h / 2f)
                )
            }

            // Thin outer rim to seat the bar visually.
            drawPath(trackPath, color = color.copy(alpha = 0.4f), style = Stroke(width = 1.2f))
        }
    }
}
