package com.techducat.irekeonibudo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.techducat.irekeonibudo.ui.theme.DeepShadow
import com.techducat.irekeonibudo.ui.theme.EmberGold

/**
 * Gives a procedurally-drawn [SceneCanvas] (or anything else) the look of a
 * framed illustration — soft drop shadow, rounded corners, and a thin gilt
 * border — instead of a bare rectangle bleeding into the background. Purely
 * decorative chrome, built from Compose modifiers only (no assets).
 */
@Composable
fun IllustrationFrame(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 14.dp, shape = RoundedCornerShape(18.dp), ambientColor = DeepShadow, spotColor = DeepShadow)
            .clip(RoundedCornerShape(18.dp))
            .background(DeepShadow)
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    listOf(
                        EmberGold.copy(alpha = 0.9f),
                        EmberGold.copy(alpha = 0.25f),
                        EmberGold.copy(alpha = 0.7f)
                    )
                ),
                shape = RoundedCornerShape(18.dp)
            )
    ) {
        content()
        // Inner hairline for a double-border, gallery-frame feel.
        Box(
            modifier = Modifier
                .matchParentSize()
                .border(width = 1.dp, color = Color.Black.copy(alpha = 0.35f), shape = RoundedCornerShape(17.dp))
        )
    }
}
