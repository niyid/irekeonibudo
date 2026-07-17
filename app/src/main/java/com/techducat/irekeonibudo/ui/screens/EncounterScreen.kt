package com.techducat.irekeonibudo.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.matchParentSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.awaitFirstDown
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.techducat.irekeonibudo.data.AttackType
import com.techducat.irekeonibudo.data.Charm
import com.techducat.irekeonibudo.data.CreaturePhase
import com.techducat.irekeonibudo.data.DodgeDirection
import com.techducat.irekeonibudo.data.DuelState
import com.techducat.irekeonibudo.data.GameState
import com.techducat.irekeonibudo.data.PlayerActionState
import com.techducat.irekeonibudo.engine.DuelEngine
import com.techducat.irekeonibudo.ui.components.AppBackground
import com.techducat.irekeonibudo.ui.components.CreatureCanvas
import com.techducat.irekeonibudo.ui.components.IllustrationFrame
import com.techducat.irekeonibudo.ui.components.PlayerStatusBar
import com.techducat.irekeonibudo.ui.components.SceneCanvas
import com.techducat.irekeonibudo.ui.components.StatBar
import com.techducat.irekeonibudo.ui.theme.BloodRed
import com.techducat.irekeonibudo.ui.theme.BoneWhite
import com.techducat.irekeonibudo.ui.theme.DeepSeaBlue
import com.techducat.irekeonibudo.ui.theme.EmberGold
import com.techducat.irekeonibudo.ui.theme.MidSeaTeal
import com.techducat.irekeonibudo.ui.theme.SpiritViolet
import kotlin.math.abs

@Composable
fun EncounterScreen(
    state: GameState,
    duel: DuelState,
    onTick: (Long) -> Unit,
    onDodge: (DodgeDirection) -> Unit,
    onBlockStart: () -> Unit,
    onBlockEnd: () -> Unit,
    onAttack: () -> Unit,
    onUseCharm: (Charm) -> Unit,
    onFlee: () -> Unit
) {
    // --- Frame loop: this is the actual game clock driving the duel ---
    LaunchedEffect(duel.outcome) {
        if (duel.outcome != null) return@LaunchedEffect
        var lastFrameNanos = 0L
        while (true) {
            val nowNanos = withFrameNanos { it }
            if (lastFrameNanos != 0L) {
                val deltaMs = ((nowNanos - lastFrameNanos) / 1_000_000L).coerceAtMost(66L) // clamp hiccups
                onTick(deltaMs)
            }
            lastFrameNanos = nowNanos
        }
    }

    AppBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            PlayerStatusBar(player = state.player)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp)
            ) {
                IllustrationFrame(modifier = Modifier.padding(top = 12.dp)) {
                    Box {
                        SceneCanvas(scene = state.currentNode.scene)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(DeepSeaBlue.copy(alpha = 0f), DeepSeaBlue.copy(alpha = 0.35f))
                                    )
                                )
                        ) {
                            CreatureCanvas(creatureId = duel.creature.id, modifier = Modifier.padding(top = 24.dp))
                        }
                        DuelOverlay(duel = duel, modifier = Modifier.matchParentSize())
                    }
                }

                Text(
                    text = duel.creature.name,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        shadow = Shadow(color = BloodRed.copy(alpha = 0.35f), offset = Offset(0f, 0f), blurRadius = 18f)
                    ),
                    modifier = Modifier.padding(top = 14.dp)
                )
                Text(
                    text = duel.creature.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
                )
                StatBar(
                    label = duel.creature.name,
                    value = duel.creatureHealth,
                    max = duel.creature.maxHealth,
                    color = BloodRed,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                val logState = rememberLazyListState()
                LaunchedEffect(duel.roundLog.size) {
                    if (duel.roundLog.isNotEmpty()) logState.animateScrollToItem(duel.roundLog.size - 1)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(DeepSeaBlue.copy(alpha = 0.55f))
                        .border(width = 1.dp, color = BloodRed.copy(alpha = 0.18f), shape = RoundedCornerShape(10.dp))
                        .padding(8.dp)
                ) {
                    LazyColumn(state = logState, modifier = Modifier.fillMaxSize()) {
                        items(duel.roundLog.takeLast(20)) { line ->
                            Text(
                                text = line,
                                style = MaterialTheme.typography.bodySmall,
                                color = BoneWhite.copy(alpha = 0.9f),
                                modifier = Modifier.padding(vertical = 1.dp)
                            )
                        }
                    }
                }

                // --- Gesture surface: swipe = dodge, hold = block, tap = attack ---
                val density = LocalDensity.current
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .padding(vertical = 10.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(MidSeaTeal.copy(alpha = 0.4f), MidSeaTeal.copy(alpha = 0.7f))
                            )
                        )
                        .border(1.dp, EmberGold.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
                        .pointerInput(duel.outcome) {
                            val dragThresholdPx = with(density) { 36.dp.toPx() }
                            val holdThresholdMs = 220L
                            awaitEachGesture {
                                val down = awaitFirstDown()
                                val downTimeMs = System.currentTimeMillis()
                                var blocking = false
                                var resolved = false
                                while (true) {
                                    val event = awaitPointerEvent()
                                    val change = event.changes.firstOrNull { it.id == down.id } ?: break
                                    if (!change.pressed) {
                                        if (!resolved) {
                                            val heldMs = System.currentTimeMillis() - downTimeMs
                                            if (blocking) onBlockEnd() else if (heldMs < holdThresholdMs) onAttack()
                                        }
                                        break
                                    }
                                    val dx = change.position.x - down.position.x
                                    if (!resolved && !blocking && abs(dx) > dragThresholdPx) {
                                        resolved = true
                                        onDodge(if (dx < 0) DodgeDirection.LEFT else DodgeDirection.RIGHT)
                                    }
                                    if (!resolved && !blocking && System.currentTimeMillis() - downTimeMs > holdThresholdMs) {
                                        blocking = true
                                        onBlockStart()
                                    }
                                    change.consume()
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "swipe to dodge · hold to block · tap to strike",
                        style = MaterialTheme.typography.labelMedium,
                        color = BoneWhite.copy(alpha = 0.55f)
                    )
                }

                CharmRow(duel = duel, charms = state.player.charms, oogun = state.player.oogun, onUseCharm = onUseCharm, onFlee = onFlee)
            }
        }
    }
}

@Composable
private fun CharmRow(
    duel: DuelState,
    charms: List<Charm>,
    oogun: Int,
    onUseCharm: (Charm) -> Unit,
    onFlee: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    ) {
        OutlinedButton(onClick = onFlee, modifier = Modifier.weight(1f)) { Text("Flee") }
        charms.filter { it != Charm.ADEORUN_TOKEN }.forEach { charm ->
            val onCooldown = duel.elapsedMs < (duel.charmCooldowns[charm] ?: 0L)
            val costsOogun = charm != Charm.HEALING_LEAF && !(charm == Charm.INNER_EYE && duel.weakPointFound)
            val affordable = !costsOogun || oogun >= DuelEngine.CHARM_OOGUN_COST
            OutlinedButton(
                onClick = { onUseCharm(charm) },
                enabled = affordable && !onCooldown,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SpiritViolet),
                modifier = Modifier.weight(1.4f)
            ) {
                Text(charm.displayName, style = MaterialTheme.typography.labelSmall, maxLines = 1)
            }
        }
    }
}

/**
 * Draws the real-time combat cues — telegraph ring, vulnerability glow, strike flash, and the
 * player's dodge/block/attack marker — layered over the creature illustration and backdrop
 * already drawn beneath it, rather than replacing them with an abstract arena.
 */
@Composable
private fun DuelOverlay(duel: DuelState, modifier: Modifier = Modifier) {
    val playerOffset = remember { Animatable(0f) }
    val playerLunge = remember { Animatable(0f) }
    val flash = remember { Animatable(0f) }

    LaunchedEffect(duel.playerAction, duel.playerDodgeDirection) {
        when (duel.playerAction) {
            PlayerActionState.DODGE_STARTUP, PlayerActionState.DODGE_ACTIVE -> {
                val target = when (duel.playerDodgeDirection) {
                    DodgeDirection.LEFT -> -1f
                    DodgeDirection.RIGHT -> 1f
                    null -> 0f
                }
                playerOffset.animateTo(target, tween(90))
            }
            PlayerActionState.DODGE_RECOVER, PlayerActionState.NEUTRAL -> playerOffset.animateTo(0f, tween(160))
            else -> Unit
        }
    }
    LaunchedEffect(duel.playerAction) {
        when (duel.playerAction) {
            PlayerActionState.ATTACK_STARTUP -> playerLunge.animateTo(1f, tween(DuelStartupMs))
            PlayerActionState.ATTACK_RECOVER, PlayerActionState.NEUTRAL -> playerLunge.animateTo(0f, tween(180))
            else -> Unit
        }
    }
    LaunchedEffect(duel.creaturePhase) {
        if (duel.creaturePhase == CreaturePhase.STRIKE) {
            flash.snapTo(1f)
            flash.animateTo(0f, tween(220))
        }
    }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Telegraph ring / directional cue over the creature
        val telegraphProgress = if (duel.creaturePhase == CreaturePhase.TELEGRAPH)
            (duel.creaturePhaseElapsedMs.toFloat() / duel.creaturePhaseDurationMs.toFloat()).coerceIn(0f, 1f) else null

        if (telegraphProgress != null && duel.telegraphType != null) {
            val color = telegraphColor(duel.telegraphType)
            val center = Offset(w * 0.72f, h * 0.4f)
            drawCircle(color.copy(alpha = 0.15f), radius = w * 0.16f, center = center)
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * (1f - telegraphProgress),
                useCenter = false,
                topLeft = Offset(center.x - w * 0.16f, center.y - w * 0.16f),
                size = androidx.compose.ui.geometry.Size(w * 0.32f, w * 0.32f),
                style = Stroke(width = 6f)
            )
        }

        // Creature vulnerability glow
        if (duel.creatureIsVulnerable) {
            drawCircle(
                color = EmberGold.copy(alpha = 0.18f),
                radius = w * 0.2f,
                center = Offset(w * 0.72f, h * 0.42f)
            )
        }

        // Strike flash
        if (flash.value > 0f) {
            drawRect(color = Color.Red.copy(alpha = flash.value * 0.25f))
        }

        // Player marker: bottom-left area, shifts with dodge, lunges forward on attack
        val baseX = w * 0.22f
        val px = baseX + playerOffset.value * w * 0.12f + playerLunge.value * w * 0.1f
        val py = h * 0.8f
        val playerColor = when (duel.playerAction) {
            PlayerActionState.BLOCKING -> SpiritViolet
            PlayerActionState.STAGGERED -> BloodRed
            else -> BoneWhite
        }
        drawCircle(color = playerColor.copy(alpha = 0.85f), radius = w * 0.04f, center = Offset(px, py))
        if (duel.playerShielded) {
            drawCircle(color = SpiritViolet.copy(alpha = 0.35f), radius = w * 0.058f, center = Offset(px, py), style = Stroke(width = 3f))
        }
    }
}

private fun telegraphColor(type: AttackType?): Color = when (type) {
    AttackType.LEFT_SWING, AttackType.RIGHT_SWING -> EmberGold
    AttackType.OVERHEAD -> BloodRed
    AttackType.LUNGE -> SpiritViolet
    null -> BoneWhite
}

private const val DuelStartupMs = 140
