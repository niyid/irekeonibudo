package com.techducat.irekeonibudo.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import com.techducat.irekeonibudo.ui.theme.DeepSeaBlue
import com.techducat.irekeonibudo.ui.theme.DeepShadow
import com.techducat.irekeonibudo.ui.theme.StarWhite
import kotlin.random.Random

/**
 * App-wide backdrop: a deep sea-to-night gradient plus a faint field of
 * star/foam specks, used behind every screen instead of a flat solid color
 * so the UI never feels like it's floating on a blank fill.
 */
@Composable
fun AppBackground(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepSeaBlue, DeepShadow)))
    ) {
        val specks = remember {
            val rng = Random(7)
            List(60) { Triple(rng.nextFloat(), rng.nextFloat(), 0.5f + rng.nextFloat() * 1.4f) }
        }
        Canvas(modifier = Modifier.fillMaxSize()) {
            specks.forEach { (xFrac, yFrac, r) ->
                drawCircle(
                    color = StarWhite.copy(alpha = 0.05f),
                    radius = r,
                    center = Offset(size.width * xFrac, size.height * yFrac)
                )
            }
        }
        content()
    }
}
