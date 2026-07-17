package com.techducat.irekeonibudo.engine

import com.techducat.irekeonibudo.data.AttackType
import com.techducat.irekeonibudo.data.Charm
import com.techducat.irekeonibudo.data.Creature
import com.techducat.irekeonibudo.data.CreaturePhase
import com.techducat.irekeonibudo.data.DodgeDirection
import com.techducat.irekeonibudo.data.DuelOutcome
import com.techducat.irekeonibudo.data.DuelState
import com.techducat.irekeonibudo.data.DuelTiming
import com.techducat.irekeonibudo.data.Player
import com.techducat.irekeonibudo.data.PlayerActionState
import kotlin.random.Random

/** Bundles the two things a duel action can change together, since charms/attacks touch both. */
data class DuelResult(val state: DuelState, val player: Player)

/**
 * Stateless, frame-driven combat resolver. GameViewModel owns the StateFlow and calls into
 * here every frame (tick) and on every player input; this class just computes the next state.
 * Kept separate from GameViewModel so the actual combat rules are unit-testable without Android.
 */
object DuelEngine {

    fun start(creature: Creature, random: Random): DuelState = DuelState(
        creature = creature,
        creatureHealth = creature.maxHealth,
        creaturePhase = CreaturePhase.IDLE,
        creaturePhaseDurationMs = randomIdleMs(random),
        roundLog = listOf("${creature.name} sizes you up.")
    )

    // --- Frame tick: advances timers, drives the creature AI, resolves strikes on schedule ---

    fun tick(state: DuelState, player: Player, deltaMs: Long, random: Random): DuelResult {
        if (state.outcome != null) return DuelResult(state, player)

        var s = state.copy(elapsedMs = state.elapsedMs + deltaMs)
        var p = player

        // --- creature state machine ---
        var creatureElapsed = s.creaturePhaseElapsedMs + deltaMs
        if (creatureElapsed >= s.creaturePhaseDurationMs) {
            when (s.creaturePhase) {
                CreaturePhase.IDLE -> {
                    val type = AttackType.entries[random.nextInt(AttackType.entries.size)]
                    s = s.copy(
                        creaturePhase = CreaturePhase.TELEGRAPH,
                        telegraphType = type,
                        creaturePhaseDurationMs = DuelTiming.TELEGRAPH_MS,
                        roundLog = s.roundLog + telegraphLine(s.creature.name, type)
                    )
                    creatureElapsed = 0L
                }
                CreaturePhase.TELEGRAPH -> {
                    // The strike instant: resolve against whatever the player is doing right now.
                    val (dmg, line, staggerCreature) = resolveCreatureStrike(s, p, random)
                    if (dmg > 0) p = p.copy(ilera = (p.ilera - dmg).coerceIn(0, 100))
                    s = s.copy(
                        creaturePhase = CreaturePhase.STRIKE,
                        creaturePhaseDurationMs = DuelTiming.STRIKE_MS,
                        playerShielded = false,
                        roundLog = s.roundLog + line
                    )
                    if (staggerCreature) {
                        // A perfectly-timed dodge staggers the creature — punish window for free.
                        s = s.copy(creaturePhase = CreaturePhase.STAGGERED, creaturePhaseDurationMs = DuelTiming.STAGGER_MS)
                    }
                    creatureElapsed = 0L
                }
                CreaturePhase.STRIKE -> {
                    s = s.copy(creaturePhase = CreaturePhase.RECOVER, creaturePhaseDurationMs = DuelTiming.RECOVER_MS)
                    creatureElapsed = 0L
                }
                CreaturePhase.RECOVER, CreaturePhase.STAGGERED -> {
                    s = s.copy(
                        creaturePhase = CreaturePhase.IDLE,
                        creaturePhaseDurationMs = randomIdleMs(random),
                        telegraphType = null
                    )
                    creatureElapsed = 0L
                }
                CreaturePhase.DEFEATED -> Unit
            }
        }
        s = s.copy(creaturePhaseElapsedMs = creatureElapsed)

        if (p.ilera <= 0 && s.outcome == null) {
            s = s.copy(outcome = DuelOutcome.DEFEAT, roundLog = s.roundLog + "Your strength gives out.")
        }

        // --- player state machine ---
        var playerElapsed = s.playerActionElapsedMs + deltaMs
        val (nextAction, resetAt, resolvedS, resolvedP) = advancePlayerAction(s, p, playerElapsed, random)
        s = resolvedS
        p = resolvedP
        s = s.copy(playerAction = nextAction, playerActionElapsedMs = if (resetAt) 0L else playerElapsed)

        return DuelResult(s, p)
    }

    private data class PlayerAdvance(val action: PlayerActionState, val reset: Boolean, val state: DuelState, val player: Player)

    private fun advancePlayerAction(state: DuelState, player: Player, elapsed: Long, random: Random): PlayerAdvance {
        val igboyaBonus = (player.igboya * DuelTiming.DODGE_ACTIVE_PER_IGBOYA_MS).toLong()
        return when (state.playerAction) {
            PlayerActionState.DODGE_STARTUP -> if (elapsed >= DuelTiming.DODGE_STARTUP_MS)
                PlayerAdvance(PlayerActionState.DODGE_ACTIVE, true, state, player) else
                PlayerAdvance(state.playerAction, false, state, player)

            PlayerActionState.DODGE_ACTIVE -> {
                val window = DuelTiming.DODGE_ACTIVE_BASE_MS + igboyaBonus
                if (elapsed >= window) PlayerAdvance(PlayerActionState.DODGE_RECOVER, true, state, player)
                else PlayerAdvance(state.playerAction, false, state, player)
            }

            PlayerActionState.DODGE_RECOVER -> if (elapsed >= DuelTiming.DODGE_RECOVER_MS)
                PlayerAdvance(PlayerActionState.NEUTRAL, true, state.copy(playerDodgeDirection = null), player) else
                PlayerAdvance(state.playerAction, false, state, player)

            PlayerActionState.ATTACK_STARTUP -> if (elapsed >= DuelTiming.ATTACK_STARTUP_MS) {
                // Active frame begins now — resolve the hit against the creature immediately.
                val (dmg, line, defeated) = resolvePlayerAttack(state, player, random)
                var s2 = state.copy(
                    creatureHealth = (state.creatureHealth - dmg).coerceAtLeast(0),
                    roundLog = state.roundLog + line
                )
                if (defeated) s2 = s2.copy(creaturePhase = CreaturePhase.DEFEATED, outcome = DuelOutcome.VICTORY)
                PlayerAdvance(PlayerActionState.ATTACK_ACTIVE, true, s2, player)
            } else PlayerAdvance(state.playerAction, false, state, player)

            PlayerActionState.ATTACK_ACTIVE -> if (elapsed >= DuelTiming.ATTACK_ACTIVE_MS)
                PlayerAdvance(PlayerActionState.ATTACK_RECOVER, true, state, player) else
                PlayerAdvance(state.playerAction, false, state, player)

            PlayerActionState.ATTACK_RECOVER -> if (elapsed >= DuelTiming.ATTACK_RECOVER_MS)
                PlayerAdvance(PlayerActionState.NEUTRAL, true, state, player) else
                PlayerAdvance(state.playerAction, false, state, player)

            PlayerActionState.STAGGERED -> if (elapsed >= DuelTiming.STAGGER_MS / 2)
                PlayerAdvance(PlayerActionState.NEUTRAL, true, state, player) else
                PlayerAdvance(state.playerAction, false, state, player)

            PlayerActionState.NEUTRAL, PlayerActionState.BLOCKING ->
                PlayerAdvance(state.playerAction, false, state, player)
        }
    }

    // --- Player-initiated actions ---

    fun dodge(state: DuelState, direction: DodgeDirection): DuelState {
        if (state.playerIsBusy || state.outcome != null) return state
        return state.copy(
            playerAction = PlayerActionState.DODGE_STARTUP,
            playerActionElapsedMs = 0L,
            playerDodgeDirection = direction
        )
    }

    fun blockStart(state: DuelState): DuelState {
        if (state.playerIsBusy || state.outcome != null) return state
        return state.copy(playerAction = PlayerActionState.BLOCKING, playerActionElapsedMs = 0L)
    }

    fun blockEnd(state: DuelState): DuelState {
        if (state.playerAction != PlayerActionState.BLOCKING) return state
        return state.copy(playerAction = PlayerActionState.NEUTRAL, playerActionElapsedMs = 0L)
    }

    fun attack(state: DuelState): DuelState {
        if (state.playerIsBusy || state.outcome != null) return state
        return state.copy(playerAction = PlayerActionState.ATTACK_STARTUP, playerActionElapsedMs = 0L)
    }

    fun flee(state: DuelState, player: Player, random: Random, whistleBoost: Boolean = false): DuelResult {
        if (state.outcome != null || state.elapsedMs < state.fleeCooldownEndMs) return DuelResult(state, player)
        val chance = (if (whistleBoost) 70 else 30) + player.igboya / 2
        val success = random.nextInt(0, 100) < chance
        return if (success) {
            DuelResult(state.copy(outcome = DuelOutcome.FLED, roundLog = state.roundLog + "You break away from ${state.creature.name}."), player)
        } else {
            DuelResult(
                state.copy(
                    fleeCooldownEndMs = state.elapsedMs + DuelTiming.WHISTLE_COOLDOWN_MS,
                    roundLog = state.roundLog + "${state.creature.name} blocks your escape!"
                ),
                player
            )
        }
    }

    fun useCharm(state: DuelState, player: Player, charm: Charm, random: Random): DuelResult {
        if (state.outcome != null) return DuelResult(state, player)
        if (charm == Charm.ADEORUN_TOKEN) return DuelResult(state, player) // lore item, not usable in a duel
        val cooldownEnd = state.charmCooldowns[charm] ?: 0L
        if (state.elapsedMs < cooldownEnd) return DuelResult(state, player)

        val costsOogun = charm != Charm.HEALING_LEAF && !(charm == Charm.INNER_EYE && state.weakPointFound)
        if (costsOogun && player.oogun < CHARM_OOGUN_COST) return DuelResult(state, player)

        var p = player
        var s = state
        if (costsOogun) p = p.clampedCopy(oogun = p.oogun - CHARM_OOGUN_COST)

        return when (charm) {
            Charm.HEALING_LEAF -> {
                if (charm !in p.charms) return DuelResult(state, player)
                val healed = random.nextInt(20, 31)
                p = p.clampedCopy(ilera = p.ilera + healed).copy(charms = p.charms - Charm.HEALING_LEAF)
                s = s.copy(roundLog = s.roundLog + "You chew the Ewé Ìwòsàn — $healed points steadier.")
                DuelResult(s, p)
            }
            Charm.CALMING_SAND -> {
                s = s.copy(
                    playerShielded = true,
                    charmCooldowns = s.charmCooldowns + (charm to s.elapsedMs + DuelTiming.AMULET_COOLDOWN_MS),
                    roundLog = s.roundLog + "You stir the Iyanrin Ìfayabalẹ̀ into the water around you — the next blow will land softer."
                )
                DuelResult(s, p)
            }
            Charm.INNER_EYE -> {
                if (!s.weakPointFound) {
                    s = s.copy(
                        weakPointFound = true,
                        roundLog = s.roundLog + "The Ojú-Inú opens — ${s.creature.name}'s tells are clear to you now."
                    )
                }
                DuelResult(s, p)
            }
            Charm.BRASS_HORN -> flee(s, p, random, whistleBoost = true)
            Charm.MOTHER_COWRIE -> {
                val hit = random.nextInt(0, 100) < (50 + p.oogun / 10)
                val dmg = if (hit) random.nextInt(10, 18) + p.oogun / 10 else 0
                val newHealth = (s.creatureHealth - dmg).coerceAtLeast(0)
                val defeated = newHealth <= 0
                s = s.copy(
                    creatureHealth = newHealth,
                    charmCooldowns = s.charmCooldowns + (charm to s.elapsedMs + DuelTiming.AMULET_COOLDOWN_MS),
                    roundLog = s.roundLog + if (hit) "You invoke ${charm.displayName} — $dmg damage!" else "You invoke ${charm.displayName}, but the ward slips off harmlessly.",
                    outcome = if (defeated) DuelOutcome.VICTORY else s.outcome,
                    creaturePhase = if (defeated) CreaturePhase.DEFEATED else s.creaturePhase
                )
                DuelResult(s, p)
            }
            Charm.ADEORUN_TOKEN -> DuelResult(s, p)
        }
    }

    // --- Resolution math ---

    /** Returns (damageToPlayer, logLine, staggerCreature). staggerCreature = a perfect dodge earns a punish window. */
    private fun resolveCreatureStrike(state: DuelState, player: Player, random: Random): Triple<Int, String, Boolean> {
        val type = state.telegraphType ?: return Triple(0, "", false)
        val name = state.creature.name
        val base = random.nextInt(state.creature.attackPower - 5, state.creature.attackPower + 6).coerceAtLeast(1)

        val dodgingActive = state.playerAction == PlayerActionState.DODGE_ACTIVE
        val dodgeDir = state.playerDodgeDirection
        val blocking = state.playerAction == PlayerActionState.BLOCKING

        val correctDodge = when (type) {
            AttackType.LEFT_SWING -> dodgingActive && dodgeDir == DodgeDirection.RIGHT
            AttackType.RIGHT_SWING -> dodgingActive && dodgeDir == DodgeDirection.LEFT
            AttackType.LUNGE -> dodgingActive
            AttackType.OVERHEAD -> false
        }
        val partialDodge = type == AttackType.OVERHEAD && dodgingActive
        val correctBlock = type == AttackType.OVERHEAD && blocking
        val partialBlock = type == AttackType.LUNGE && blocking

        var damage = base
        var line: String
        var stagger = false

        when {
            correctDodge -> {
                damage = 0
                stagger = true
                line = perfectDodgeLine(name, type)
            }
            correctBlock -> {
                damage = (base * 0.25).toInt()
                line = "You raise your guard — ${name}'s blow lands for a reduced $damage damage."
            }
            partialDodge -> {
                damage = (base * 0.5).toInt()
                line = "You throw yourself aside — only $damage damage gets through."
            }
            partialBlock -> {
                damage = (base * 0.6).toInt()
                line = "Your guard turns most of the thrust aside — $damage damage."
            }
            else -> {
                line = if (state.playerAction == PlayerActionState.NEUTRAL) {
                    "You're caught flat-footed — $name lands a full hit for $damage damage!"
                } else {
                    "Wrong read — $name gets through your guard for $damage damage!"
                }
            }
        }

        if (state.playerShielded && damage > 0) {
            damage = damage / 2
            line += " The calming sand softens the blow."
        }

        return Triple(damage, line, stagger)
    }

    /** Returns (damageToCreature, logLine, defeated). */
    private fun resolvePlayerAttack(state: DuelState, player: Player, random: Random): Triple<Int, String, Boolean> {
        val name = state.creature.name
        val weakPointBonus = if (state.weakPointFound) 4 else 0
        var damage = random.nextInt(8, 16) + player.igboya / 10 + weakPointBonus

        val line: String
        when (state.creaturePhase) {
            CreaturePhase.STAGGERED -> {
                damage = (damage * 2.0).toInt()
                line = "$name is staggered wide open — a brutal $damage damage!"
            }
            CreaturePhase.RECOVER -> {
                damage = (damage * 1.5).toInt()
                line = "You catch $name still recovering — $damage damage!"
            }
            else -> {
                line = "You strike $name for $damage damage."
            }
        }
        val newHealth = (state.creatureHealth - damage).coerceAtLeast(0)
        return Triple(damage, line, newHealth <= 0)
    }

    private fun randomIdleMs(random: Random): Long =
        random.nextLong(DuelTiming.CREATURE_IDLE_MIN_MS, DuelTiming.CREATURE_IDLE_MAX_MS)

    private fun telegraphLine(name: String, type: AttackType): String = when (type) {
        AttackType.LEFT_SWING -> "$name winds up a swing from your left — dodge right!"
        AttackType.RIGHT_SWING -> "$name winds up a swing from your right — dodge left!"
        AttackType.OVERHEAD -> "$name rears up for an overhead blow — block it!"
        AttackType.LUNGE -> "$name coils for a lunge — sidestep it!"
    }

    private fun perfectDodgeLine(name: String, type: AttackType): String = when (type) {
        AttackType.LEFT_SWING, AttackType.RIGHT_SWING -> "You slip the swing entirely — $name overextends, wide open!"
        AttackType.LUNGE -> "You sidestep the lunge clean — $name is off-balance!"
        AttackType.OVERHEAD -> "You slip the overhead blow — $name is off-balance!"
    }

    const val CHARM_OOGUN_COST = 10
}
