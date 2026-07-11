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
import com.techducat.irekeonibudo.ui.theme.BloodRed
import com.techducat.irekeonibudo.ui.theme.BoneWhite
import com.techducat.irekeonibudo.ui.theme.CoralPink
import com.techducat.irekeonibudo.ui.theme.DeepCurrentBlue
import com.techducat.irekeonibudo.ui.theme.DeepSeaBlue
import com.techducat.irekeonibudo.ui.theme.EmberGold
import com.techducat.irekeonibudo.ui.theme.EmberOrange
import com.techducat.irekeonibudo.ui.theme.SeaFoamTeal
import com.techducat.irekeonibudo.ui.theme.SpiritViolet
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Renders a stylized, procedurally-generated illustration for the current
 * [SceneType]. Everything here is original vector art built from primitive
 * shapes plus gradients/glow — no imported assets, no photos, no third-party IP.
 *
 * [SceneType] is a reused label set (VILLAGE / FOREST_PATH / RIVER / CAVE /
 * SPIRIT_COURT / VICTORY / DEATH) rather than one custom-built for this story,
 * so what each label draws is chosen to fit how StoryData.kt actually uses it:
 * FOREST_PATH covers the Mountain of Trials, RIVER covers open and deep water
 * (storm, shark, sunken wreck approach), CAVE covers both the wreck's hold and
 * Itanforiti's lit den, and SPIRIT_COURT covers both Arogidigba's coral throne
 * and the royal court of Àlùpàyìdá.
 */
@Composable
fun SceneCanvas(scene: SceneType, modifier: Modifier = Modifier) {
    // Seeded per scene type so waves/bubbles/etc. don't jitter on every recomposition.
    val seed = remember(scene) { scene.ordinal * 977 + 13 }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 10f)
            .background(backgroundBrush(scene))
    ) {
        val rng = Random(seed)
        when (scene) {
            SceneType.VILLAGE -> drawTown(rng)
            SceneType.FOREST_PATH -> drawMountainTrail(rng)
            SceneType.RIVER -> drawOpenWater(rng)
            SceneType.CAVE -> drawHollow(rng)
            SceneType.SPIRIT_COURT -> drawCourt(rng)
            SceneType.VICTORY -> drawVictory(rng)
            SceneType.DEATH -> drawDeath(rng)
        }
        drawVignette()
    }
}

private fun backgroundBrush(scene: SceneType): Brush = when (scene) {
    SceneType.VILLAGE -> Brush.verticalGradient(listOf(Color(0xFF4A3B26), Color(0xFF17140D)))
    SceneType.FOREST_PATH -> Brush.verticalGradient(listOf(Color(0xFF3A4B57), Color(0xFF13212A)))
    SceneType.RIVER -> Brush.verticalGradient(listOf(Color(0xFF1B3E4C), DeepSeaBlue))
    SceneType.CAVE -> Brush.verticalGradient(listOf(Color(0xFF1B1F22), Color(0xFF07070A)))
    SceneType.SPIRIT_COURT -> Brush.verticalGradient(listOf(SpiritViolet.copy(alpha = 0.55f), DeepSeaBlue))
    SceneType.VICTORY -> Brush.verticalGradient(listOf(Color(0xFF3A2E13), EmberGold.copy(alpha = 0.3f), DeepSeaBlue))
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

/** A two-tone gradient silhouette with a soft rim highlight — trees on land, kelp underwater. */
private fun DrawScope.drawFrondSilhouette(x: Float, baseY: Float, height: Float, baseColor: Color, highlight: Color = baseColor) {
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

/** VILLAGE: Ìrèké's home town, and later Àlùpàyìdá's market/shrine square — a coastal town, sea at the horizon. */
private fun DrawScope.drawTown(rng: Random) {
    val w = size.width
    val h = size.height
    drawSun(w * 0.82f, h * 0.2f, w * 0.055f, EmberOrange)
    // Sea horizon behind the rooftops instead of forest.
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(DeepCurrentBlue.copy(alpha = 0.5f), DeepCurrentBlue.copy(alpha = 0.15f)),
            startY = h * 0.55f,
            endY = h * 0.72f
        ),
        topLeft = Offset(0f, h * 0.55f),
        size = Size(w, h * 0.17f)
    )
    drawHut(w * 0.25f, h * 0.85f, w * 0.16f, h * 0.22f)
    drawHut(w * 0.5f, h * 0.88f, w * 0.2f, h * 0.26f)
    drawHut(w * 0.75f, h * 0.85f, w * 0.15f, h * 0.2f)
    // A couple of tall, sparse palms rather than forest canopy — a coastal town, not a hunter's clearing.
    repeat(3) {
        drawFrondSilhouette(
            w * (0.05f + rng.nextFloat() * 0.9f), h * 0.95f, h * (0.18f + rng.nextFloat() * 0.08f),
            baseColor = SeaFoamTeal, highlight = SeaFoamTeal.copy(alpha = 0.75f)
        )
    }
    drawLine(BoneWhite.copy(alpha = 0.25f), Offset(0f, h * 0.95f), Offset(w, h * 0.95f), strokeWidth = 3f)
}

/** FOREST_PATH: the Mountain of Trials — jagged peaks, a switchback trail, thin high-altitude air. */
private fun DrawScope.drawMountainTrail(rng: Random) {
    val w = size.width
    val h = size.height

    // Distant, pale peak layer.
    val backPeaks = Path().apply {
        moveTo(0f, h * 0.55f)
        val points = 5
        for (i in 0..points) {
            val x = w * i / points
            val y = h * (0.3f + rng.nextFloat() * 0.15f)
            lineTo(x, y)
        }
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(backPeaks, color = Color(0xFF4A5C68).copy(alpha = 0.55f))

    // Nearer, darker peak layer.
    val frontPeaks = Path().apply {
        moveTo(0f, h * 0.75f)
        val points = 6
        for (i in 0..points) {
            val x = w * i / points
            val y = h * (0.45f + rng.nextFloat() * 0.2f)
            lineTo(x, y)
        }
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(
        frontPeaks,
        brush = Brush.verticalGradient(listOf(Color(0xFF283840), Color(0xFF0F1A20)), startY = h * 0.45f, endY = h)
    )
    // Snow-hint on the ridgeline.
    drawLine(BoneWhite.copy(alpha = 0.3f), Offset(w * 0.15f, h * 0.5f), Offset(w * 0.4f, h * 0.42f), strokeWidth = 2f)
    drawLine(BoneWhite.copy(alpha = 0.22f), Offset(w * 0.6f, h * 0.48f), Offset(w * 0.85f, h * 0.4f), strokeWidth = 2f)

    // Thin, wind-bent trees — sparse, not a canopy; air's too thin up here for more.
    repeat(4) { i ->
        val x = w * (0.1f + i * 0.26f) + rng.nextFloat() * w * 0.05f
        drawFrondSilhouette(x, h * 0.95f, h * (0.12f + rng.nextFloat() * 0.08f), baseColor = Color(0xFF2E3A2F), highlight = Color(0xFF44543F))
    }

    // A switchback trail climbing the slope.
    val trail = Path().apply {
        moveTo(w * 0.55f, h)
        cubicTo(w * 0.3f, h * 0.85f, w * 0.7f, h * 0.65f, w * 0.4f, h * 0.5f)
        cubicTo(w * 0.2f, h * 0.4f, w * 0.55f, h * 0.3f, w * 0.5f, h * 0.18f)
    }
    drawPath(trail, color = Color(0xFF8C9A93).copy(alpha = 0.45f), style = Stroke(width = w * 0.028f))

    // Thin pale light, high and cold.
    drawGlow(Offset(w * 0.68f, h * 0.12f), w * 0.35f, BoneWhite, intensity = 0.15f)
}

/** RIVER: open and deep water — the storm, the shark, the swim down toward the sunken court. */
private fun DrawScope.drawOpenWater(rng: Random) {
    val w = size.width
    val h = size.height

    // Sunlit shafts filtering down from a surface we may or may not be able to see.
    repeat(3) { i ->
        val x = w * (0.2f + i * 0.32f) + rng.nextFloat() * w * 0.06f
        val shaft = Path().apply {
            moveTo(x - w * 0.05f, 0f)
            lineTo(x + w * 0.05f, 0f)
            lineTo(x + w * 0.14f, h)
            lineTo(x - w * 0.14f, h)
            close()
        }
        drawPath(
            shaft,
            brush = Brush.verticalGradient(
                colors = listOf(BoneWhite.copy(alpha = 0.10f), Color.Transparent),
                startY = 0f,
                endY = h * 0.9f
            )
        )
    }

    // A wavy surface line near the top — works whether we're just under it or well below.
    val surface = Path().apply {
        moveTo(0f, h * 0.08f)
        for (x in 0..20) {
            val fx = w * x / 20f
            val fy = h * 0.08f + sin(x * 0.6f) * h * 0.015f
            lineTo(fx, fy)
        }
    }
    drawPath(surface, color = BoneWhite.copy(alpha = 0.25f), style = Stroke(width = 3f))

    // Drifting kelp / current-grass silhouettes near the seabed.
    repeat(4) { i ->
        drawFrondSilhouette(
            w * (0.1f + i * 0.28f), h * 0.98f, h * (0.22f + rng.nextFloat() * 0.12f),
            baseColor = DeepSeaBlue.copy(alpha = 0.7f), highlight = SeaFoamTeal.copy(alpha = 0.45f)
        )
    }

    // Rising bubbles, sized and spaced by the seeded rng so they hold still between recompositions.
    repeat(24) {
        val r = 1.5f + rng.nextFloat() * 3f
        drawCircle(
            BoneWhite.copy(alpha = 0.12f + rng.nextFloat() * 0.18f),
            radius = r,
            center = Offset(rng.nextFloat() * w, rng.nextFloat() * h)
        )
    }

    // A faint distant wreck silhouette for depth and story grounding.
    val hull = Path().apply {
        moveTo(w * 0.55f, h * 0.7f)
        lineTo(w * 0.95f, h * 0.68f)
        lineTo(w * 0.88f, h * 0.8f)
        lineTo(w * 0.5f, h * 0.82f)
        close()
    }
    drawPath(hull, color = Color(0xFF0A1A22).copy(alpha = 0.6f))
    drawLine(Color(0xFF0A1A22).copy(alpha = 0.6f), Offset(w * 0.65f, h * 0.7f), Offset(w * 0.68f, h * 0.5f), strokeWidth = 4f)
}

/** CAVE: the wreck's flooded hold, and — with a warmer palette — Itanforiti's lit-from-within den. */
private fun DrawScope.drawHollow(rng: Random) {
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
    // A warm, steady glow — "lit from within by a light that no torch makes."
    drawGlow(Offset(w * 0.5f, h * 0.5f), w * 0.22f, EmberGold, intensity = 0.24f)
    repeat(18) {
        drawCircle(
            BoneWhite.copy(alpha = rng.nextFloat() * 0.45f), radius = 1.5f,
            center = Offset(rng.nextFloat() * w, rng.nextFloat() * h * 0.4f)
        )
    }
}

/** SPIRIT_COURT: Arogidigba's coral-and-anchor-chain throne, and Àlùpàyìdá's royal court. */
private fun DrawScope.drawCourt(rng: Random) {
    val w = size.width
    val h = size.height
    drawGlow(Offset(w * 0.5f, h * 0.45f), w * 0.5f, SpiritViolet, intensity = 0.32f)
    drawGlow(Offset(w * 0.5f, h * 0.55f), w * 0.3f, CoralPink, intensity = 0.14f)
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
    // Gold rim catching whatever light there is — coral court or royal one, it's still a throne.
    drawLine(EmberGold.copy(alpha = 0.35f), Offset(w * 0.42f, h * 0.45f), Offset(w * 0.5f, h * 0.3f), strokeWidth = 2f)
    drawLine(EmberGold.copy(alpha = 0.35f), Offset(w * 0.58f, h * 0.45f), Offset(w * 0.5f, h * 0.3f), strokeWidth = 2f)
    repeat(10) { i ->
        val angle = (i / 10f) * 2 * Math.PI
        val r = w * 0.35f
        val x = w * 0.5f + (r * cos(angle)).toFloat()
        val y = h * 0.85f + (r * 0.25f * sin(angle)).toFloat()
        val tint = if (i % 3 == 0) CoralPink else SpiritViolet
        drawGlow(Offset(x, y), 16f, tint, intensity = 0.4f)
        drawCircle(tint.copy(alpha = 0.7f), radius = 5f, center = Offset(x, y))
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
    // A simple crown silhouette on the horizon, in place of a hunter's forest — this story ends in a throne room.
    val crownY = h * 0.92f
    val crownW = w * 0.34f
    val crownX = w * 0.5f
    val crown = Path().apply {
        moveTo(crownX - crownW / 2, crownY)
        lineTo(crownX - crownW / 2, crownY - h * 0.05f)
        lineTo(crownX - crownW * 0.3f, crownY - h * 0.13f)
        lineTo(crownX - crownW * 0.12f, crownY - h * 0.06f)
        lineTo(crownX, crownY - h * 0.17f)
        lineTo(crownX + crownW * 0.12f, crownY - h * 0.06f)
        lineTo(crownX + crownW * 0.3f, crownY - h * 0.13f)
        lineTo(crownX + crownW / 2, crownY - h * 0.05f)
        lineTo(crownX + crownW / 2, crownY)
        close()
    }
    drawPath(
        crown,
        brush = Brush.verticalGradient(
            colors = listOf(EmberGold, Color(0xFF8A5F1E)),
            startY = crownY - h * 0.17f,
            endY = crownY
        )
    )
}

private fun DrawScope.drawDeath(rng: Random) {
    val w = size.width
    val h = size.height
    drawGlow(Offset(w * 0.5f, h * 0.42f), w * 0.32f, BloodRed, intensity = 0.3f)
    // A broken mast rather than a dead forest — this story's dangers are mostly at sea.
    repeat(2) { i ->
        val x = w * (0.3f + i * 0.4f)
        val height = h * (0.45f + rng.nextFloat() * 0.2f)
        drawLine(Color(0xFF1A0505), Offset(x, h * 0.98f), Offset(x + w * 0.05f, h * 0.98f - height), strokeWidth = w * 0.02f)
    }
    drawCircle(BloodRed.copy(alpha = 0.28f), radius = w * 0.18f, center = Offset(w * 0.5f, h * 0.4f))
}
