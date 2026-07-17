package com.techducat.irekeonibudo.data

/** How the creature is threatening the player right now. */
enum class AttackType { LEFT_SWING, RIGHT_SWING, OVERHEAD, LUNGE }

/** Which way a swipe/dodge went. */
enum class DodgeDirection { LEFT, RIGHT }

/** Creature's real-time state machine. */
enum class CreaturePhase { IDLE, TELEGRAPH, STRIKE, RECOVER, STAGGERED, DEFEATED }

/** What the player's body is currently doing (drives both hit-resolution and animation). */
enum class PlayerActionState { NEUTRAL, DODGE_STARTUP, DODGE_ACTIVE, DODGE_RECOVER, BLOCKING, ATTACK_STARTUP, ATTACK_ACTIVE, ATTACK_RECOVER, STAGGERED }

/**
 * A single line of the duel round log, as structured facts rather than
 * pre-rendered text. [com.techducat.irekeonibudo.engine.DuelEngine] is a
 * plain Kotlin class with no Android dependency (kept unit-testable without
 * Android), so it can't call getString() itself — the UI layer resolves each
 * event to localized display text via string resources (see EncounterScreen).
 */
sealed class DuelLogEvent {
    data object CreatureSizesUp : DuelLogEvent()
    data class Telegraph(val type: AttackType) : DuelLogEvent()
    data class PerfectDodge(val type: AttackType) : DuelLogEvent()
    data class BlockedHit(val damage: Int, val shielded: Boolean = false) : DuelLogEvent()
    data class PartialDodge(val damage: Int, val shielded: Boolean = false) : DuelLogEvent()
    data class PartialBlock(val damage: Int, val shielded: Boolean = false) : DuelLogEvent()
    data class FlatFooted(val damage: Int, val shielded: Boolean = false) : DuelLogEvent()
    data class WrongRead(val damage: Int, val shielded: Boolean = false) : DuelLogEvent()
    data object StrengthGivesOut : DuelLogEvent()
    data class StaggerHit(val damage: Int) : DuelLogEvent()
    data class RecoverHit(val damage: Int) : DuelLogEvent()
    data class NormalHit(val damage: Int) : DuelLogEvent()
    data object FleeSuccess : DuelLogEvent()
    data object FleeBlocked : DuelLogEvent()
    data class LeafHeal(val healed: Int) : DuelLogEvent()
    data object SandShield : DuelLogEvent()
    data object EyeOpens : DuelLogEvent()
    data class CowrieHit(val damage: Int) : DuelLogEvent()
    data object CowrieMiss : DuelLogEvent()
    /** Should never actually surface in the log — guards the (unreachable) missing-telegraph case. */
    data object None : DuelLogEvent()
}

/**
 * Real-time duel state, ticked every frame by [com.techducat.irekeonibudo.engine.DuelEngine].
 * Replaces the old turn-based EncounterState — combat here resolves from timing/positioning,
 * not RNG hit-chance rolls.
 */
data class DuelState(
    val creature: Creature,
    val creatureHealth: Int,
    val creaturePhase: CreaturePhase = CreaturePhase.IDLE,
    val creaturePhaseElapsedMs: Long = 0L,
    val creaturePhaseDurationMs: Long = 0L,
    val telegraphType: AttackType? = null,

    val playerAction: PlayerActionState = PlayerActionState.NEUTRAL,
    val playerActionElapsedMs: Long = 0L,
    val playerDodgeDirection: DodgeDirection? = null,
    val playerShielded: Boolean = false,
    val weakPointFound: Boolean = false,

    val comboCount: Int = 0,
    val roundLog: List<DuelLogEvent> = emptyList(),
    val outcome: DuelOutcome? = null,

    /** Wall-clock cooldown gates (ms since duel start) per charm, keyed by name to avoid a map-of-enum headache in UI diffing. */
    val charmCooldowns: Map<Charm, Long> = emptyMap(),
    /** Wall-clock gate (ms since duel start) below which another flee attempt is refused. */
    val fleeCooldownEndMs: Long = 0L,
    val elapsedMs: Long = 0L
) {
    val creatureIsDefeated: Boolean get() = creatureHealth <= 0
    /** Creature is punishable — attacking now deals bonus damage. */
    val creatureIsVulnerable: Boolean get() = creaturePhase == CreaturePhase.RECOVER || creaturePhase == CreaturePhase.STAGGERED
    /** Player is mid-action and can't start a new one. */
    val playerIsBusy: Boolean get() = playerAction != PlayerActionState.NEUTRAL && playerAction != PlayerActionState.BLOCKING
}

enum class DuelOutcome { VICTORY, DEFEAT, FLED }

/** Tunable timing constants — the real-time equivalent of the old hit-chance companion object. */
object DuelTiming {
    const val CREATURE_IDLE_MIN_MS = 500L
    const val CREATURE_IDLE_MAX_MS = 1100L
    const val TELEGRAPH_MS = 650L
    const val STRIKE_MS = 120L
    const val RECOVER_MS = 550L
    const val STAGGER_MS = 1100L

    const val DODGE_STARTUP_MS = 60L
    const val DODGE_ACTIVE_BASE_MS = 220L
    const val DODGE_ACTIVE_PER_IGBOYA_MS = 0.8 // up to +80ms at igboya 100
    const val DODGE_RECOVER_MS = 200L

    const val ATTACK_STARTUP_MS = 140L
    const val ATTACK_ACTIVE_MS = 90L
    const val ATTACK_RECOVER_MS = 260L

    const val AMULET_COOLDOWN_MS = 1500L
    const val WHISTLE_COOLDOWN_MS = 1000L
}
