package com.techducat.irekeonibudo.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.techducat.irekeonibudo.data.SceneType
import com.techducat.irekeonibudo.ui.components.SceneCanvas
import com.techducat.irekeonibudo.ui.theme.AshGrey
import com.techducat.irekeonibudo.ui.theme.EmberGold
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun TitleScreen(
    hasSave: suspend () -> Boolean,
    onNewGame: () -> Unit,
    onContinue: () -> Unit
) {
    var saveExists by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { saveExists = hasSave() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            SceneCanvas(scene = SceneType.FOREST_PATH)
            EmberOverlay(modifier = Modifier.matchParentSize())
        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = "ÌRÈKÉ ONÍBÙDÓ",
            style = MaterialTheme.typography.headlineLarge.copy(
                shadow = Shadow(color = EmberGold.copy(alpha = 0.45f), offset = Offset(0f, 0f), blurRadius = 24f)
            ),
            textAlign = TextAlign.Center
        )
        Text(
            text = "From Shipwreck to Crown\nAn Orphan's Journey Through Deep Water and Wisdom",
            style = MaterialTheme.typography.bodyMedium,
            color = AshGrey,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
        )
        Button(onClick = onNewGame, modifier = Modifier.fillMaxWidth(0.8f)) {
            Text("New Journey")
        }
        Spacer(Modifier.height(12.dp))
        if (saveExists) {
            OutlinedButton(onClick = onContinue, modifier = Modifier.fillMaxWidth(0.8f)) {
                Text("Continue")
            }
        }
    }
}

/**
 * A handful of slow-rising embers drifting up over the title illustration.
 * Purely decorative, purely procedural (no assets) — a fixed seed keeps the
 * particle layout stable across recompositions within a session.
 */
@Composable
private fun EmberOverlay(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "embers")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "emberPhase"
    )

    // (horizontal position 0..1, phase offset 0..1, relative size)
    val embers = remember {
        val rng = Random(42)
        List(16) { Triple(rng.nextFloat(), rng.nextFloat(), 0.6f + rng.nextFloat() * 0.8f) }
    }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        embers.forEach { (xFrac, phaseOffset, sizeScale) ->
            val t = (phase + phaseOffset) % 1f
            val y = h * (1f - t)
            val drift = sin((t * 2 * Math.PI).toFloat()) * w * 0.02f
            val x = (w * xFrac + drift).coerceIn(0f, w)
            val alpha = sin((t * Math.PI).toFloat()).coerceIn(0f, 1f)
            drawCircle(
                color = EmberGold.copy(alpha = alpha * 0.85f),
                radius = 2.5f * sizeScale,
                center = Offset(x, y)
            )
        }
    }
}
