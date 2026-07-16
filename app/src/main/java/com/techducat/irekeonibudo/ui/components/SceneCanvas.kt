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
import androidx.compose.ui.graphics.PathEffect
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
import com.techducat.irekeonibudo.ui.theme.FoamWhite
import com.techducat.irekeonibudo.ui.theme.KelpGreen
import com.techducat.irekeonibudo.ui.theme.MoonSilver
import com.techducat.irekeonibudo.ui.theme.ScaleGreen
import com.techducat.irekeonibudo.ui.theme.SeaFoamTeal
import com.techducat.irekeonibudo.ui.theme.SpiritViolet
import com.techducat.irekeonibudo.ui.theme.StarWhite
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
        drawFilmGrain(rng, alpha = 0.035f)
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

/** A faint scatter of noise dots — breaks up flat gradient banding, gives the art some grain/texture. */
private fun DrawScope.drawFilmGrain(rng: Random, alpha: Float) {
    val w = size.width
    val h = size.height
    repeat(140) {
        val x = rng.nextFloat() * w
        val y = rng.nextFloat() * h
        val a = rng.nextFloat() * alpha
        drawCircle(Color.White.copy(alpha = a), radius = 0.9f, center = Offset(x, y))
    }
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

/** A scatter of twinkling points, denser near the top of the frame. */
private fun DrawScope.drawStars(rng: Random, count: Int, bandTop: Float = 0f, bandBottom: Float = 0.55f) {
    val w = size.width
    val h = size.height
    repeat(count) {
        val x = rng.nextFloat() * w
        val y = h * (bandTop + rng.nextFloat() * (bandBottom - bandTop))
        val r = 0.6f + rng.nextFloat() * 1.4f
        val a = 0.25f + rng.nextFloat() * 0.55f
        drawCircle(StarWhite.copy(alpha = a), radius = r, center = Offset(x, y))
        if (r > 1.6f) {
            // A tiny four-point sparkle on the brighter stars.
            drawLine(StarWhite.copy(alpha = a * 0.6f), Offset(x - r * 2.5f, y), Offset(x + r * 2.5f, y), strokeWidth = 0.7f)
            drawLine(StarWhite.copy(alpha = a * 0.6f), Offset(x, y - r * 2.5f), Offset(x, y + r * 2.5f), strokeWidth = 0.7f)
        }
    }
}

/** Soft, layered cloud puffs made from overlapping circles. */
private fun DrawScope.drawCloud(cx: Float, cy: Float, scale: Float, color: Color, alpha: Float) {
    val puffs = listOf(
        Triple(-1.0f, 0.1f, 0.55f),
        Triple(-0.4f, -0.15f, 0.75f),
        Triple(0.25f, -0.1f, 0.85f),
        Triple(0.9f, 0.05f, 0.5f)
    )
    puffs.forEach { (dx, dy, r) ->
        drawCircle(
            color = color.copy(alpha = alpha),
            radius = scale * r,
            center = Offset(cx + dx * scale, cy + dy * scale)
        )
    }
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
    // A thin rim-light edge so the canopy doesn't read as a flat cutout.
    drawPath(canopyPath, color = highlight.copy(alpha = 0.4f), style = Stroke(width = 1.4f))
}

private fun DrawScope.drawHut(x: Float, baseY: Float, w: Float, h: Float) {
    drawRect(
        brush = Brush.verticalGradient(listOf(Color(0xFF8A5F3A), Color(0xFF5A3D22))),
        topLeft = Offset(x - w / 2, baseY - h),
        size = Size(w, h)
    )
    // Wall-plank texture — a few faint vertical seams.
    for (i in 1..3) {
        val lx = x - w / 2 + w * i / 4f
        drawLine(Color.Black.copy(alpha = 0.12f), Offset(lx, baseY - h), Offset(lx, baseY), strokeWidth = 1f)
    }
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
    drawPath(roof, color = EmberGold.copy(alpha = 0.18f), style = Stroke(width = 1.2f))
    // Doorway glow — a warm hint of a hearth inside.
    drawGlow(Offset(x, baseY - h * 0.35f), w * 0.35f, EmberOrange, intensity = 0.35f)
    drawRect(
        color = Color(0xFF1A0F06),
        topLeft = Offset(x - w * 0.12f, baseY - h * 0.5f),
        size = Size(w * 0.24f, h * 0.5f)
    )
}

/** VILLAGE: Ìrèké's home town, and later Àlùpàyìdá's market/shrine square — a coastal town, sea at the horizon. */
private fun DrawScope.drawTown(rng: Random) {
    val w = size.width
    val h = size.height
    drawSun(w * 0.82f, h * 0.2f, w * 0.055f, EmberOrange)
    // A couple of drifting clouds catching the sunset light.
    drawCloud(w * 0.28f, h * 0.18f, w * 0.09f, BoneWhite, 0.12f)
    drawCloud(w * 0.55f, h * 0.12f, w * 0.07f, BoneWhite, 0.09f)
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
    // Glinting reflection of the sun on the water.
    for (i in 0..6) {
        val ry = h * 0.58f + i * h * 0.02f
        val rw = w * (0.16f - i * 0.018f)
        drawLine(
            EmberOrange.copy(alpha = 0.22f - i * 0.03f),
            Offset(w * 0.82f - rw, ry),
            Offset(w * 0.82f + rw, ry),
            strokeWidth = 2f
        )
    }
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
    // A few small birds for life in the sky.
    repeat(3) { i ->
        val bx = w * (0.1f + i * 0.12f)
        val by = h * (0.12f + rng.nextFloat() * 0.1f)
        val bird = Path().apply {
            moveTo(bx - 6f, by)
            quadraticTo(bx - 2f, by - 4f, bx, by)
            quadraticTo(bx + 2f, by - 4f, bx + 6f, by)
        }
        drawPath(bird, color = Color(0xFF1A140C).copy(alpha = 0.5f), style = Stroke(width = 1.4f))
    }
    drawLine(BoneWhite.copy(alpha = 0.25f), Offset(0f, h * 0.95f), Offset(w, h * 0.95f), strokeWidth = 3f)
}

/** FOREST_PATH: the Mountain of Trials — jagged peaks, a switchback trail, thin high-altitude air. */
private fun DrawScope.drawMountainTrail(rng: Random) {
    val w = size.width
    val h = size.height

    drawStars(rng, count = 30, bandTop = 0f, bandBottom = 0.28f)

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
    // Snow-hint on the ridgeline, plus a jagged snow cap.
    drawLine(BoneWhite.copy(alpha = 0.3f), Offset(w * 0.15f, h * 0.5f), Offset(w * 0.4f, h * 0.42f), strokeWidth = 2f)
    drawLine(BoneWhite.copy(alpha = 0.22f), Offset(w * 0.6f, h * 0.48f), Offset(w * 0.85f, h * 0.4f), strokeWidth = 2f)
    val snowCap = Path().apply {
        moveTo(w * 0.32f, h * 0.44f)
        lineTo(w * 0.38f, h * 0.38f)
        lineTo(w * 0.44f, h * 0.44f)
        lineTo(w * 0.4f, h * 0.42f)
        lineTo(w * 0.36f, h * 0.45f)
        close()
    }
    drawPath(snowCap, color = BoneWhite.copy(alpha = 0.4f))

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
    drawPath(
        trail,
        color = BoneWhite.copy(alpha = 0.15f),
        style = Stroke(width = w * 0.006f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(w * 0.02f, w * 0.02f)))
    )

    // Drifting mist banks at the base of the ridge.
    drawCloud(w * 0.2f, h * 0.68f, w * 0.16f, MoonSilver, 0.1f)
    drawCloud(w * 0.75f, h * 0.72f, w * 0.14f, MoonSilver, 0.08f)

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

    // A wavy surface line near the top, with a brighter foam crest — works whether
    // we're just under it or well below.
    val surface = Path().apply {
        moveTo(0f, h * 0.08f)
        for (x in 0..20) {
            val fx = w * x / 20f
            val fy = h * 0.08f + sin(x * 0.6f) * h * 0.015f
            lineTo(fx, fy)
        }
    }
    drawPath(surface, color = FoamWhite.copy(alpha = 0.32f), style = Stroke(width = 3f))
    drawPath(surface, color = BoneWhite.copy(alpha = 0.14f), style = Stroke(width = 8f))

    // A second, slower swell line beneath it for a sense of layered current.
    val swell = Path().apply {
        moveTo(0f, h * 0.16f)
        for (x in 0..20) {
            val fx = w * x / 20f
            val fy = h * 0.16f + sin(x * 0.4f + 1.5f) * h * 0.012f
            lineTo(fx, fy)
        }
    }
    drawPath(swell, color = SeaFoamTeal.copy(alpha = 0.22f), style = Stroke(width = 2f))

    // Drifting kelp / current-grass silhouettes near the seabed.
    repeat(4) { i ->
        drawFrondSilhouette(
            w * (0.1f + i * 0.28f), h * 0.98f, h * (0.22f + rng.nextFloat() * 0.12f),
            baseColor = KelpGreen.copy(alpha = 0.85f), highlight = SeaFoamTeal.copy(alpha = 0.5f)
        )
    }

    // Rising bubbles, sized and spaced by the seeded rng so they hold still between recompositions.
    repeat(30) {
        val r = 1.2f + rng.nextFloat() * 3.2f
        val c = Offset(rng.nextFloat() * w, rng.nextFloat() * h)
        drawCircle(BoneWhite.copy(alpha = 0.10f + rng.nextFloat() * 0.16f), radius = r, center = c)
        // A tiny highlight fleck on the larger bubbles for a glassy look.
        if (r > 2.6f) {
            drawCircle(FoamWhite.copy(alpha = 0.35f), radius = r * 0.3f, center = c + Offset(-r * 0.3f, -r * 0.3f))
        }
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
    // A few small fish shapes for scale and life.
    repeat(4) { i ->
        val fx = w * (0.15f + rng.nextFloat() * 0.7f)
        val fy = h * (0.35f + rng.nextFloat() * 0.35f)
        val fishScale = 5f + rng.nextFloat() * 4f
        val fish = Path().apply {
            moveTo(fx - fishScale, fy)
            quadraticTo(fx, fy - fishScale * 0.7f, fx + fishScale * 1.6f, fy)
            quadraticTo(fx, fy + fishScale * 0.7f, fx - fishScale, fy)
            close()
        }
        drawPath(fish, color = SeaFoamTeal.copy(alpha = 0.3f))
    }
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
    // Craggy rock texture along the cave mouth's inner edge.
    drawPath(mouth, color = Color(0xFF2A2A30).copy(alpha = 0.5f), style = Stroke(width = 3f))
    // A few stalactites hinted at the top of the mouth.
    repeat(5) { i ->
        val x = w * (0.28f + i * 0.11f)
        val len = h * (0.05f + rng.nextFloat() * 0.06f)
        drawLine(Color(0xFF0D0D10), Offset(x, h * 0.08f), Offset(x, h * 0.08f + len), strokeWidth = 3f)
    }
    // A warm, steady glow — "lit from within by a light that no torch makes."
    drawGlow(Offset(w * 0.5f, h * 0.5f), w * 0.24f, EmberGold, intensity = 0.26f)
    drawGlow(Offset(w * 0.5f, h * 0.55f), w * 0.1f, StarWhite, intensity = 0.2f)
    repeat(24) {
        drawCircle(
            BoneWhite.copy(alpha = rng.nextFloat() * 0.45f), radius = 1.5f,
            center = Offset(rng.nextFloat() * w, rng.nextFloat() * h * 0.4f)
        )
    }
    // Floating motes near the light source, drifting slightly outward.
    repeat(10) {
        val angle = rng.nextFloat() * 2f * Math.PI.toFloat()
        val dist = rng.nextFloat() * w * 0.2f
        val x = w * 0.5f + cos(angle) * dist
        val y = h * 0.5f + sin(angle) * dist * 0.6f
        drawCircle(EmberGold.copy(alpha = 0.4f), radius = 1.2f, center = Offset(x, y))
    }
}

/** SPIRIT_COURT: Arogidigba's coral-and-anchor-chain throne, and Àlùpàyìdá's royal court. */
private fun DrawScope.drawCourt(rng: Random) {
    val w = size.width
    val h = size.height
    drawGlow(Offset(w * 0.5f, h * 0.45f), w * 0.5f, SpiritViolet, intensity = 0.32f)
    drawGlow(Offset(w * 0.5f, h * 0.55f), w * 0.3f, CoralPink, intensity = 0.14f)
    // A ring of tall coral/pillar silhouettes framing the throne, for scale and grandeur.
    repeat(6) { i ->
        val x = w * (0.08f + i * 0.17f)
        if (x in (w * 0.35f)..(w * 0.65f)) return@repeat
        val ph = h * (0.35f + rng.nextFloat() * 0.15f)
        val pillar = Path().apply {
            moveTo(x - w * 0.02f, h * 0.92f)
            lineTo(x - w * 0.015f, h * 0.92f - ph)
            lineTo(x + w * 0.015f, h * 0.92f - ph)
            lineTo(x + w * 0.02f, h * 0.92f)
            close()
        }
        drawPath(
            pillar,
            brush = Brush.verticalGradient(
                listOf(SpiritViolet.copy(alpha = 0.5f), Color(0xFF1A1424).copy(alpha = 0.7f))
            )
        )
    }
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
    drawLine(EmberGold.copy(alpha = 0.4f), Offset(w * 0.42f, h * 0.45f), Offset(w * 0.5f, h * 0.3f), strokeWidth = 2.5f)
    drawLine(EmberGold.copy(alpha = 0.4f), Offset(w * 0.58f, h * 0.45f), Offset(w * 0.5f, h * 0.3f), strokeWidth = 2.5f)
    // A carved diamond motif on the throne's face.
    val motif = Path().apply {
        moveTo(w * 0.5f, h * 0.55f)
        lineTo(w * 0.47f, h * 0.63f)
        lineTo(w * 0.5f, h * 0.71f)
        lineTo(w * 0.53f, h * 0.63f)
        close()
    }
    drawPath(motif, color = EmberGold.copy(alpha = 0.25f), style = Stroke(width = 1.5f))
    repeat(12) { i ->
        val angle = (i / 12f) * 2 * Math.PI
        val r = w * 0.36f
        val x = w * 0.5f + (r * cos(angle)).toFloat()
        val y = h * 0.85f + (r * 0.25f * sin(angle)).toFloat()
        val tint = if (i % 3 == 0) CoralPink else SpiritViolet
        drawGlow(Offset(x, y), 16f, tint, intensity = 0.4f)
        drawCircle(tint.copy(alpha = 0.7f), radius = 5f, center = Offset(x, y))
        drawCircle(StarWhite.copy(alpha = 0.5f), radius = 1.4f, center = Offset(x, y))
    }
}

private fun DrawScope.drawVictory(rng: Random) {
    val w = size.width
    val h = size.height
    drawSun(w * 0.5f, h * 0.35f, w * 0.13f, EmberGold)
    repeat(16) { i ->
        val angle = (i / 16f) * 2 * Math.PI
        val x1 = w * 0.5f + (w * 0.17f * cos(angle)).toFloat()
        val y1 = h * 0.35f + (w * 0.17f * sin(angle)).toFloat()
        val x2 = w * 0.5f + (w * 0.27f * cos(angle)).toFloat()
        val y2 = h * 0.35f + (w * 0.27f * sin(angle)).toFloat()
        drawLine(EmberGold.copy(alpha = 0.75f), Offset(x1, y1), Offset(x2, y2), strokeWidth = 3.5f)
    }
    // Drifting confetti-like glints, upward-biased.
    repeat(20) {
        val x = rng.nextFloat() * w
        val y = rng.nextFloat() * h * 0.7f
        val r = 1.5f + rng.nextFloat() * 2.5f
        drawCircle((if (it % 2 == 0) EmberGold else BoneWhite).copy(alpha = 0.5f), radius = r, center = Offset(x, y))
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
    drawPath(crown, color = StarWhite.copy(alpha = 0.5f), style = Stroke(width = 1.5f))
    // Gems along the crown's base.
    listOf(-0.3f, -0.12f, 0f, 0.12f, 0.3f).forEach { frac ->
        drawCircle(
            if (frac == 0f) BloodRed.copy(alpha = 0.85f) else SpiritViolet.copy(alpha = 0.75f),
            radius = w * 0.012f,
            center = Offset(crownX + crownW * frac, crownY - h * 0.045f)
        )
    }
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
    // Tattered sail-cloth remnants hanging from one mast.
    val tatter = Path().apply {
        moveTo(w * 0.3f, h * 0.6f)
        lineTo(w * 0.42f, h * 0.62f)
        lineTo(w * 0.4f, h * 0.72f)
        lineTo(w * 0.34f, h * 0.7f)
        lineTo(w * 0.36f, h * 0.8f)
        lineTo(w * 0.3f, h * 0.78f)
        close()
    }
    drawPath(tatter, color = Color(0xFF261010).copy(alpha = 0.6f))
    drawCircle(BloodRed.copy(alpha = 0.28f), radius = w * 0.18f, center = Offset(w * 0.5f, h * 0.4f))
    // Slow-drifting ash/embers.
    repeat(14) {
        val x = rng.nextFloat() * w
        val y = rng.nextFloat() * h
        drawCircle(BloodRed.copy(alpha = 0.18f + rng.nextFloat() * 0.15f), radius = 1.2f, center = Offset(x, y))
    }
}

// --- Creature illustrations (combat) ---------------------------------------

/**
 * Renders a stylized silhouette of a story creature for the encounter
 * screen. Keyed off the creature's data id; falls back to a generic
 * serpentine shape for anything not explicitly designed.
 */
@Composable
fun CreatureCanvas(creatureId: String, modifier: Modifier = Modifier) {
    val seed = remember(creatureId) { creatureId.hashCode() }
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 7f)
    ) {
        val rng = Random(seed)
        when (creatureId) {
            "guard_shark" -> drawSharkSilhouette(rng)
            "arogidigba" -> drawSeaQueenSilhouette(rng)
            "flying_python" -> drawWingedSerpentSilhouette(rng)
            else -> drawGenericSerpentSilhouette(rng)
        }
    }
}

private fun DrawScope.drawSharkSilhouette(rng: Random) {
    val w = size.width
    val h = size.height
    drawGlow(Offset(w * 0.5f, h * 0.55f), w * 0.42f, DeepSeaBlue, intensity = 0.35f)
    // Bubbles for context.
    repeat(14) {
        drawCircle(
            BoneWhite.copy(alpha = 0.12f + rng.nextFloat() * 0.15f),
            radius = 1f + rng.nextFloat() * 2f,
            center = Offset(rng.nextFloat() * w, rng.nextFloat() * h)
        )
    }
    val cx = w * 0.52f
    val cy = h * 0.55f
    val body = Path().apply {
        moveTo(cx - w * 0.32f, cy)
        cubicTo(cx - w * 0.2f, cy - h * 0.22f, cx + w * 0.12f, cy - h * 0.2f, cx + w * 0.34f, cy - h * 0.02f)
        cubicTo(cx + w * 0.12f, cy + h * 0.2f, cx - w * 0.2f, cy + h * 0.22f, cx - w * 0.32f, cy)
        close()
    }
    drawPath(
        body,
        brush = Brush.linearGradient(
            listOf(Color(0xFF3A3F42), Color(0xFF14181A)),
            start = Offset(cx, cy - h * 0.2f),
            end = Offset(cx, cy + h * 0.2f)
        )
    )
    // Dorsal fin.
    val fin = Path().apply {
        moveTo(cx - w * 0.02f, cy - h * 0.18f)
        lineTo(cx + w * 0.03f, cy - h * 0.4f)
        lineTo(cx + w * 0.08f, cy - h * 0.16f)
        close()
    }
    drawPath(fin, color = Color(0xFF14181A))
    // Tail fin.
    val tail = Path().apply {
        moveTo(cx - w * 0.32f, cy)
        lineTo(cx - w * 0.44f, cy - h * 0.14f)
        lineTo(cx - w * 0.38f, cy)
        lineTo(cx - w * 0.44f, cy + h * 0.14f)
        close()
    }
    drawPath(tail, color = Color(0xFF14181A))
    // Gill slashes and a hint of teeth.
    repeat(3) { i ->
        drawLine(
            Color.Black.copy(alpha = 0.4f),
            Offset(cx + w * (0.14f + i * 0.03f), cy - h * 0.05f),
            Offset(cx + w * (0.11f + i * 0.03f), cy + h * 0.06f),
            strokeWidth = 1.5f
        )
    }
    drawCircle(BloodRed.copy(alpha = 0.85f), radius = w * 0.01f, center = Offset(cx + w * 0.28f, cy - h * 0.03f))
    // Bronze scale glints along the back per the story's description.
    repeat(10) {
        val x = cx + (rng.nextFloat() - 0.3f) * w * 0.5f
        val y = cy - h * 0.1f + rng.nextFloat() * h * 0.15f
        drawCircle(EmberGold.copy(alpha = 0.18f), radius = 1.5f, center = Offset(x, y))
    }
}

private fun DrawScope.drawSeaQueenSilhouette(rng: Random) {
    val w = size.width
    val h = size.height
    drawGlow(Offset(w * 0.5f, h * 0.5f), w * 0.48f, SpiritViolet, intensity = 0.4f)
    drawGlow(Offset(w * 0.5f, h * 0.5f), w * 0.2f, CoralPink, intensity = 0.2f)
    val cx = w * 0.5f
    // Flowing gown/tail merging into the water — regal silhouette, not literal anatomy.
    val gown = Path().apply {
        moveTo(cx, h * 0.14f)
        cubicTo(cx - w * 0.16f, h * 0.3f, cx - w * 0.3f, h * 0.5f, cx - w * 0.22f, h * 0.92f)
        lineTo(cx + w * 0.22f, h * 0.92f)
        cubicTo(cx + w * 0.3f, h * 0.5f, cx + w * 0.16f, h * 0.3f, cx, h * 0.14f)
        close()
    }
    drawPath(
        gown,
        brush = Brush.verticalGradient(listOf(SpiritViolet, Color(0xFF241A38)), startY = h * 0.14f, endY = h * 0.92f)
    )
    // A crown of coral points.
    repeat(5) { i ->
        val fx = cx - w * 0.08f + i * w * 0.04f
        val fh = h * (0.06f + (if (i == 2) 0.04f else 0f))
        drawLine(EmberGold.copy(alpha = 0.6f), Offset(fx, h * 0.14f), Offset(fx, h * 0.14f - fh), strokeWidth = 2f)
    }
    // Long nails / clawed hands hinted at the gown's sides.
    drawGlow(Offset(cx - w * 0.24f, h * 0.5f), w * 0.05f, CoralPink, intensity = 0.5f)
    drawGlow(Offset(cx + w * 0.24f, h * 0.5f), w * 0.05f, CoralPink, intensity = 0.5f)
    // Face suggestion: moon-round glow with two dark eyes.
    drawCircle(BoneWhite.copy(alpha = 0.15f), radius = w * 0.07f, center = Offset(cx, h * 0.24f))
    drawCircle(Color.Black.copy(alpha = 0.7f), radius = w * 0.01f, center = Offset(cx - w * 0.025f, h * 0.24f))
    drawCircle(Color.Black.copy(alpha = 0.7f), radius = w * 0.01f, center = Offset(cx + w * 0.025f, h * 0.24f))
    // Drifting motes of sea-magic around her.
    repeat(16) {
        val angle = rng.nextFloat() * 2f * Math.PI.toFloat()
        val dist = w * (0.15f + rng.nextFloat() * 0.3f)
        val x = cx + cos(angle) * dist
        val y = h * 0.5f + sin(angle) * dist * 0.7f
        drawCircle(CoralPink.copy(alpha = 0.35f), radius = 1.3f, center = Offset(x, y))
    }
}

private fun DrawScope.drawWingedSerpentSilhouette(rng: Random) {
    val w = size.width
    val h = size.height
    drawGlow(Offset(w * 0.5f, h * 0.4f), w * 0.5f, SpiritViolet.copy(alpha = 0.6f), intensity = 0.3f)
    val cy = h * 0.5f
    // Serpentine body as a sine-wave ribbon.
    val body = Path()
    val topEdge = mutableListOf<Offset>()
    val bottomEdge = mutableListOf<Offset>()
    for (i in 0..40) {
        val t = i / 40f
        val x = w * t
        val y = cy + sin(t * 3f * Math.PI.toFloat()) * h * 0.14f
        val thickness = h * (0.09f - 0.05f * t)
        topEdge.add(Offset(x, y - thickness))
        bottomEdge.add(Offset(x, y + thickness))
    }
    body.moveTo(topEdge.first().x, topEdge.first().y)
    topEdge.drop(1).forEach { body.lineTo(it.x, it.y) }
    bottomEdge.asReversed().forEach { body.lineTo(it.x, it.y) }
    body.close()
    drawPath(body, brush = Brush.horizontalGradient(listOf(ScaleGreen, Color(0xFF16260F))))
    // Cloud-torn wings, drawn as two ragged triangular fans mid-body.
    listOf(-1f, 1f).forEach { side ->
        val originX = w * 0.32f
        val originY = cy + sin(0.32f * 3f * Math.PI.toFloat()) * h * 0.14f
        val wing = Path().apply {
            moveTo(originX, originY)
            lineTo(originX + side * w * 0.02f, originY - h * 0.32f)
            lineTo(originX + side * w * 0.14f, originY - h * 0.22f)
            lineTo(originX + side * w * 0.08f, originY - h * 0.1f)
            lineTo(originX + side * w * 0.2f, originY - h * 0.02f)
            lineTo(originX, originY + h * 0.05f)
            close()
        }
        drawPath(wing, color = MoonSilver.copy(alpha = 0.22f))
        drawPath(wing, color = MoonSilver.copy(alpha = 0.4f), style = Stroke(width = 1.2f))
    }
    // Head with a hint of jaw and eye at the wide end.
    val headX = topEdge.first().x
    val headY = cy
    drawCircle(Color(0xFF16260F), radius = h * 0.1f, center = Offset(headX, headY))
    drawCircle(BloodRed.copy(alpha = 0.85f), radius = w * 0.008f, center = Offset(headX + w * 0.02f, headY - h * 0.02f))
    // Wisps of cloud trailing the tail.
    repeat(10) {
        val x = w * (0.75f + rng.nextFloat() * 0.25f)
        val y = cy + sin(1f * 3f * Math.PI.toFloat()) * h * 0.05f + (rng.nextFloat() - 0.5f) * h * 0.1f
        drawCircle(BoneWhite.copy(alpha = 0.1f), radius = 3f + rng.nextFloat() * 4f, center = Offset(x, y))
    }
}

private fun DrawScope.drawGenericSerpentSilhouette(rng: Random) {
    val w = size.width
    val h = size.height
    drawGlow(Offset(w * 0.5f, h * 0.5f), w * 0.4f, BloodRed, intensity = 0.25f)
    val cy = h * 0.5f
    val body = Path()
    body.moveTo(0f, cy)
    for (i in 0..30) {
        val t = i / 30f
        body.lineTo(w * t, cy + sin(t * 4f * Math.PI.toFloat()) * h * 0.1f)
    }
    drawPath(body, color = Color(0xFF3A3A3A), style = Stroke(width = h * 0.1f))
    drawCircle(BloodRed.copy(alpha = 0.7f), radius = w * 0.012f, center = Offset(w * 0.06f, cy))
}
