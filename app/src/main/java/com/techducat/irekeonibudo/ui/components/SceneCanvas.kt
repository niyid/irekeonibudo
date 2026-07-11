package com.techducat.irekeonibudo.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.techducat.irekeonibudo.data.SceneType
import com.techducat.irekeonibudo.ui.theme.AshGrey
import com.techducat.irekeonibudo.ui.theme.BloodRed
import com.techducat.irekeonibudo.ui.theme.BoneWhite
import com.techducat.irekeonibudo.ui.theme.EmberGold
import com.techducat.irekeonibudo.ui.theme.EmberOrange
import com.techducat.irekeonibudo.ui.theme.ForestCanopy
import com.techducat.irekeonibudo.ui.theme.ForestDeepGreen
import com.techducat.irekeonibudo.ui.theme.ForestMidGreen
import com.techducat.irekeonibudo.ui.theme.RiverBlue
import com.techducat.irekeonibudo.ui.theme.SpiritViolet
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Renders a stylized, procedurally-generated illustration for the current
 * [SceneType]. Everything here is original vector art built from primitive
 * shapes plus gradients/glow — no imported assets, no photos, no third-party IP.
 */
@Composable
fun SceneCanvas(scene: SceneType, modifier: Modifier = Modifier) {
    // Seeded per scene type so trees/stars/etc. don't jitter on every recomposition.
    val seed = remember(scene) { scene.ordinal * 977 + 13 }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 10f)
            .background(backgroundBrush(scene))
    ) {
        val rng = Random(seed)
        when (scene) {
            SceneType.VILLAGE -> drawVillage(rng)
            SceneType.FOREST_PATH -> drawForest(rng)
            SceneType.RIVER -> drawRiver(rng)
            SceneType.CAVE -> drawCave(rng)
            SceneType.SPIRIT_COURT -> drawSpiritCourt(rng)
            SceneType.VICTORY -> drawVictory(rng)
            SceneType.DEATH -> drawDeath(rng)
        }
        drawVignette()
    }
}

private fun backgroundBrush(scene: SceneType): Brush = when (scene) {
    SceneType.VILLAGE -> Brush.verticalGradient(listOf(Color(0xFF4A3B26), Color(0xFF17140D)))
    SceneType.FOREST_PATH -> Brush.verticalGradient(listOf(ForestMidGreen, ForestDeepGreen))
    SceneType.RIVER -> Brush.verticalGradient(listOf(Color(0xFF2B4750), RiverBlue.copy(alpha = 0.65f)))
    SceneType.CAVE -> Brush.verticalGradient(listOf(Color(0xFF232329), Color(0xFF07070A)))
    SceneType.SPIRIT_COURT -> Brush.verticalGradient(listOf(SpiritViolet.copy(alpha = 0.55f), ForestDeepGreen))
    SceneType.VICTORY -> Brush.verticalGradient(listOf(Color(0xFF3A2E13), EmberGold.copy(alpha = 0.3f), ForestDeepGreen))
    SceneType.DEATH -> Brush.verticalGradient(listOf(Color(0xFF33090C), Color.Black))
}

// --- Shared polish helpers -------------------------------------------------

/** Soft radial bloom — the cheap, all-API-level substitute for a real blur. */
private fun DrawScope.drawGlow(center: Offset, radius: Float, color: Color, intensity: Float = 0.6f) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(color.copy(alpha = intensity), color.copy(alpha = 0f)),
            center = center,
            radius = radius
        ),
        radius = radius,
        center = center
    )
}

/** Darkens the frame edges slightly so the eye settles on the illustration's center. */
private fun DrawScope.drawVignette() {
    val w = size.width
    val h = size.height
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.38f)),
            center = Offset(w / 2f, h * 0.42f),
            radius = maxOf(w, h) * 0.85f
        )
    )
}

private fun DrawScope.drawSun(cx: Float, cy: Float, r: Float, color: Color) {
    drawGlow(Offset(cx, cy), r * 3.2f, color, intensity = 0.5f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(BoneWhite.copy(alpha = 0.85f), color),
            center = Offset(cx, cy),
            radius = r
        ),
        radius = r,
        center = Offset(cx, cy)
    )
}

/** A two-tone gradient canopy with a soft rim highlight, instead of a flat silhouette. */
private fun DrawScope.drawTreeSilhouette(x: Float, baseY: Float, height: Float, baseColor: Color, highlight: Color = baseColor) {
    val trunkWidth = height * 0.08f
    drawRect(
        brush = Brush.verticalGradient(listOf(Color(0xFF2A1B10), Color(0xFF120B06))),
        topLeft = Offset(x - trunkWidth / 2, baseY - height * 0.3f),
        size = Size(trunkWidth, height * 0.3f)
    )
    val canopyPath = Path().apply {
        moveTo(x, baseY - height)
        lineTo(x - height * 0.32f, baseY - height * 0.25f)
        lineTo(x + height * 0.32f, baseY - height * 0.25f)
        close()
    }
    drawPath(
        canopyPath,
        brush = Brush.linearGradient(
            colors = listOf(highlight, baseColor),
            start = Offset(x, baseY - height),
            end = Offset(x, baseY - height * 0.25f)
        )
    )
}

private fun DrawScope.drawHut(x: Float, baseY: Float, w: Float, h: Float) {
    drawRect(
        brush = Brush.verticalGradient(listOf(Color(0xFF8A5F3A), Color(0xFF5A3D22))),
        topLeft = Offset(x - w / 2, baseY - h),
        size = Size(w, h)
    )
    val roof = Path().apply {
        moveTo(x - w * 0.65f, baseY - h)
        lineTo(x, baseY - h - h * 0.7f)
        lineTo(x + w * 0.65f, baseY - h)
        close()
    }
    drawPath(
        roof,
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFF5A3E24), Color(0xFF2A1C10)),
            start = Offset(x, baseY - h - h * 0.7f),
            end = Offset(x, baseY - h)
        )
    )
    // Doorway glow — a warm hint of a hearth inside.
    drawGlow(Offset(x, baseY - h * 0.35f), w * 0.35f, EmberOrange, intensity = 0.35f)
}

private fun DrawScope.drawVillage(rng: Random) {
    val w = size.width
    val h = size.height
    drawSun(w * 0.82f, h * 0.2f, w * 0.055f, EmberOrange)
    drawHut(w * 0.25f, h * 0.85f, w * 0.16f, h * 0.22f)
    drawHut(w * 0.5f, h * 0.88f, w * 0.2f, h * 0.26f)
    drawHut(w * 0.75f, h * 0.85f, w * 0.15f, h * 0.2f)
    repeat(4) {
        drawTreeSilhouette(
            w * (0.05f + rng.nextFloat() * 0.9f), h * 0.95f, h * (0.15f + rng.nextFloat() * 0.1f),
            baseColor = ForestCanopy, highlight = ForestCanopy.copy(alpha = 0.75f)
        )
    }
    drawLine(BoneWhite.copy(alpha = 0.25f), Offset(0f, h * 0.95f), Offset(w, h * 0.95f), strokeWidth = 3f)
}

private fun DrawScope.drawForest(rng: Random) {
    val w = size.width
    val h = size.height
    // Distant canopy layer
    repeat(9) { i ->
        val x = w * (i / 9f) + rng.nextFloat() * (w / 9f)
        drawTreeSilhouette(
            x, h * 0.9f, h * (0.35f + rng.nextFloat() * 0.25f),
            baseColor = ForestCanopy.copy(alpha = 0.55f), highlight = ForestCanopy.copy(alpha = 0.32f)
        )
    }
    // Near canopy layer, darker & taller
    repeat(6) { i ->
        val x = w * (i / 6f) + rng.nextFloat() * (w / 6f)
        drawTreeSilhouette(x, h * 0.98f, h * (0.5f + rng.nextFloat() * 0.3f), baseColor = ForestDeepGreen, highlight = ForestCanopy.copy(alpha = 0.5f))
    }
    // A faint shaft of light through the canopy for depth.
    drawGlow(Offset(w * 0.62f, h * 0.15f), w * 0.4f, EmberGold, intensity = 0.18f)
    // A winding path
    val path = Path().apply {
        moveTo(w * 0.5f, h)
        cubicTo(w * 0.35f, h * 0.75f, w * 0.65f, h * 0.55f, w * 0.5f, h * 0.35f)
    }
    drawPath(path, color = Color(0xFF5C4A32).copy(alpha = 0.5f), style = Stroke(width = w * 0.05f))
}

private fun DrawScope.drawRiver(rng: Random) {
    val w = size.width
    val h = size.height
    repeat(4) { i ->
        drawTreeSilhouette(w * (0.1f + i * 0.28f), h * 0.55f, h * 0.28f, baseColor = ForestDeepGreen.copy(alpha = 0.6f), highlight = ForestCanopy.copy(alpha = 0.4f))
    }
    // river band with wavy top edge, gradient depth
    val top = Path().apply {
        moveTo(0f, h * 0.6f)
        for (x in 0..20) {
            val fx = w * x / 20f
            val fy = h * 0.6f + sin(x * 0.6f) * h * 0.02f
            lineTo(fx, fy)
        }
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(
        top,
        brush = Brush.verticalGradient(
            colors = listOf(RiverBlue.copy(alpha = 0.9f), Color(0xFF162E33).copy(alpha = 0.95f)),
            startY = h * 0.6f,
            endY = h
        )
    )
    // Glinting highlight band, like light catching the current.
    drawLine(BoneWhite.copy(alpha = 0.2f), Offset(0f, h * 0.63f), Offset(w, h * 0.63f), strokeWidth = 2f)
    repeat(5) { i ->
        val y = h * (0.68f + i * 0.055f)
        drawLine(AshGrey.copy(alpha = 0.3f), Offset(0f, y), Offset(w, y), strokeWidth = 2f)
    }
}

private fun DrawScope.drawCave(rng: Random) {
    val w = size.width
    val h = size.height
    val mouth = Path().apply {
        moveTo(w * 0.15f, h)
        cubicTo(w * 0.1f, h * 0.35f, w * 0.4f, h * 0.05f, w * 0.5f, h * 0.05f)
        cubicTo(w * 0.6f, h * 0.05f, w * 0.9f, h * 0.35f, w * 0.85f, h)
        close()
    }
    drawPath(
        mouth,
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF141418), Color.Black),
            center = Offset(w * 0.5f, h * 0.55f),
            radius = w * 0.55f
        )
    )
    // A faint deep glow, as if something waits further in.
    drawGlow(Offset(w * 0.5f, h * 0.5f), w * 0.22f, SpiritViolet, intensity = 0.22f)
    repeat(20) {
        drawCircle(
            BoneWhite.copy(alpha = rng.nextFloat() * 0.5f), radius = 1.5f,
            center = Offset(rng.nextFloat() * w, rng.nextFloat() * h * 0.4f)
        )
    }
}

private fun DrawScope.drawSpiritCourt(rng: Random) {
    val w = size.width
    val h = size.height
    drawGlow(Offset(w * 0.5f, h * 0.45f), w * 0.5f, SpiritViolet, intensity = 0.35f)
    // throne silhouette center back
    val throne = Path().apply {
        moveTo(w * 0.42f, h * 0.9f)
        lineTo(w * 0.42f, h * 0.45f)
        lineTo(w * 0.5f, h * 0.3f)
        lineTo(w * 0.58f, h * 0.45f)
        lineTo(w * 0.58f, h * 0.9f)
        close()
    }
    drawPath(
        throne,
        brush = Brush.verticalGradient(listOf(Color(0xFF3A2C4D), Color(0xFF1A1424)), startY = h * 0.3f, endY = h * 0.9f)
    )
    repeat(10) { i ->
        val angle = (i / 10f) * 2 * Math.PI
        val r = w * 0.35f
        val x = w * 0.5f + (r * cos(angle)).toFloat()
        val y = h * 0.85f + (r * 0.25f * sin(angle)).toFloat()
        drawGlow(Offset(x, y), 16f, SpiritViolet, intensity = 0.45f)
        drawCircle(SpiritViolet.copy(alpha = 0.75f), radius = 5f, center = Offset(x, y))
    }
}

private fun DrawScope.drawVictory(rng: Random) {
    val w = size.width
    val h = size.height
    drawSun(w * 0.5f, h * 0.35f, w * 0.13f, EmberGold)
    repeat(12) { i ->
        val angle = (i / 12f) * 2 * Math.PI
        val x1 = w * 0.5f + (w * 0.17f * cos(angle)).toFloat()
        val y1 = h * 0.35f + (w * 0.17f * sin(angle)).toFloat()
        val x2 = w * 0.5f + (w * 0.26f * cos(angle)).toFloat()
        val y2 = h * 0.35f + (w * 0.26f * sin(angle)).toFloat()
        drawLine(EmberGold.copy(alpha = 0.75f), Offset(x1, y1), Offset(x2, y2), strokeWidth = 4f)
    }
    repeat(3) { i ->
        drawTreeSilhouette(w * (0.2f + i * 0.3f), h * 0.98f, h * 0.3f, baseColor = ForestDeepGreen, highlight = ForestCanopy.copy(alpha = 0.5f))
    }
}

private fun DrawScope.drawDeath(rng: Random) {
    val w = size.width
    val h = size.height
    drawGlow(Offset(w * 0.5f, h * 0.42f), w * 0.32f, BloodRed, intensity = 0.3f)
    repeat(3) { i ->
        drawTreeSilhouette(
            w * (0.15f + i * 0.35f), h * 0.95f, h * (0.45f + rng.nextFloat() * 0.2f),
            baseColor = Color(0xFF1A0505), highlight = Color(0xFF2E0B0B)
        )
    }
    drawCircle(BloodRed.copy(alpha = 0.28f), radius = w * 0.18f, center = Offset(w * 0.5f, h * 0.4f))
}
