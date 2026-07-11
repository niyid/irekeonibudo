package com.techducat.irekeonibudo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techducat.irekeonibudo.data.Charm
import com.techducat.irekeonibudo.data.Choice
import com.techducat.irekeonibudo.data.EncounterState
import com.techducat.irekeonibudo.data.GameRepository
import com.techducat.irekeonibudo.data.GameState
import com.techducat.irekeonibudo.data.Player
import com.techducat.irekeonibudo.data.Screen
import com.techducat.irekeonibudo.data.Stat
import com.techducat.irekeonibudo.data.StatEffect
import com.techducat.irekeonibudo.data.StoryData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel(private val repository: GameRepository) : ViewModel() {

    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    private val random = Random(System.currentTimeMillis())

    companion object {
        // Central difficulty knobs — tune balance here rather than hunting magic numbers below.
        private const val PLAYER_BASE_HIT_CHANCE = 45   // + igboya / 2
        private const val PLAYER_CRIT_CHANCE = 12        // % chance a landed hit doubles
        private const val ENEMY_BASE_HIT_CHANCE = 55     // - oogun / 10 (player's evasive skill)
        private const val ENEMY_MIN_HIT_CHANCE = 20      // floor so high OOGUN can't make fights trivial
        private const val ENEMY_CRIT_CHANCE = 8

        // Oogun is documented (see Player) as "spent to cast protective/attacking charms" —
        // enforce that here so it's a real resource, not just a passive combat-math input.
        const val CHARM_OOGUN_COST = 10
        private const val COWRIE_HIT_CHANCE = 50          // + oogun / 10; a charm, not a certainty
    }

    /** True if [charm] can currently be invoked in combat (enough oogun, correct turn, etc). */
    fun canAffordCharm(charm: Charm, player: Player): Boolean =
        charm != Charm.ADEORUN_TOKEN && (charm == Charm.HEALING_LEAF || player.oogun >= CHARM_OOGUN_COST)

    fun startNewGame() {
        val player = Player()
        _state.value = GameState(
            screen = Screen.STORY,
            player = player,
            currentNode = StoryData.nodes.getValue(player.currentNodeId)
        )
    }

    fun continueGame() = viewModelScope.launch {
        val loaded = repository.load() ?: return@launch startNewGame()
        val node = StoryData.nodes[loaded.currentNodeId] ?: StoryData.nodes.getValue(StoryData.START_NODE)
        _state.value = _state.value.copy(
            screen = when {
                node.isEnding -> Screen.ENDING
                node.encounterId != null -> Screen.ENCOUNTER
                else -> Screen.STORY
            },
            player = loaded,
            currentNode = node,
            activeEncounter = node.encounterId?.let { encounterId ->
                val creature = StoryData.creatures.getValue(encounterId)
                EncounterState(creature = creature, creatureHealth = creature.maxHealth)
            }
        )
    }

    suspend fun hasSaveSuspend(): Boolean = repository.hasSave()

    /** Persists progress mid-story. Endings call [clearSave] instead — a finished run isn't resumable. */
    fun saveGame() = viewModelScope.launch {
        repository.save(_state.value.player)
    }

    private fun clearSave() = viewModelScope.launch {
        repository.deleteSave()
    }

    /** Apply a story choice: run stat effects, grant flags, move to the next node. */
    fun choose(choice: Choice) {
        val current = _state.value
        var player = current.player
        for (effect in choice.effects) player = applyEffect(player, effect)
        choice.setsFlag?.let { flag -> player = player.copy(flags = player.flags + flag) }

        val nextNode = StoryData.nodes.getValue(choice.nextNodeId)
        player = player.copy(
            currentNodeId = nextNode.id,
            visitedNodes = player.visitedNodes + nextNode.id
        )
        nextNode.grantsCharm?.let { charm ->
            if (charm !in player.charms) player = player.copy(charms = player.charms + charm)
        }

        val enteringEncounter = nextNode.encounterId != null
        _state.value = current.copy(
            player = player,
            currentNode = nextNode,
            screen = when {
                enteringEncounter -> Screen.ENCOUNTER
                nextNode.isEnding -> Screen.ENDING
                else -> Screen.STORY
            },
            activeEncounter = if (enteringEncounter) {
                val creature = StoryData.creatures.getValue(nextNode.encounterId!!)
                EncounterState(creature = creature, creatureHealth = creature.maxHealth)
            } else null
        )
        when {
            nextNode.isEnding -> clearSave()
            !enteringEncounter -> saveGame()
        }
    }

    // --- Combat ---

    fun encounterAttack() = resolveEncounterTurn { player, encounter ->
        val hit = random.nextInt(0, 100) < (PLAYER_BASE_HIT_CHANCE + player.igboya / 2)
        val scoutedBeforehand = "spotted_python_weakness" in player.flags && encounter.creature.id == "flying_python"
        val weakPointBonus = if (encounter.weakPointFound || scoutedBeforehand) 8 else 0
        val isCrit = hit && random.nextInt(0, 100) < PLAYER_CRIT_CHANCE
        val baseDamage = random.nextInt(8, 16) + player.igboya / 10 + weakPointBonus
        val damage = if (hit) (if (isCrit) baseDamage * 2 else baseDamage) else 0
        val newHealth = (encounter.creatureHealth - damage).coerceAtLeast(0)
        val name = encounter.creature.name
        val log = when {
            isCrit -> pick(
                "A perfect shot! You catch $name off guard for a brutal $damage damage!",
                "Everything lines up — $damage damage, and $name staggers."
            )
            hit && weakPointBonus > 0 -> "You drive your shot straight into the seam you spotted — $damage damage!"
            hit -> pick(
                "You strike $name for $damage damage.",
                "Your shot lands true — $damage damage to $name.",
                "$name reels from a solid hit — $damage damage."
            )
            else -> pick(
                "Your shot goes wide — $name dodges.",
                "$name twists aside at the last moment.",
                "You misjudge the angle and the shot goes wild."
            )
        }
        encounter.copy(creatureHealth = newHealth, roundLog = encounter.roundLog + log, playerTurn = false)
    }

    fun encounterUseCharm(charm: Charm) {
        val current = _state.value
        val encounter = current.activeEncounter ?: return
        if (!encounter.playerTurn) return
        if (!canAffordCharm(charm, current.player)) return

        // Deduct cost (if any) up front, before delegating to resolveEncounterTurn/encounterFlee,
        // which capture their own snapshot of state and would otherwise clobber a deduction
        // made from inside their closures.
        when (charm) {
            Charm.HEALING_LEAF -> Unit // herbal, not cast — no oogun cost
            Charm.INNER_EYE -> if (!encounter.weakPointFound) spendOogun(CHARM_OOGUN_COST) // free if already scouted
            else -> spendOogun(CHARM_OOGUN_COST)
        }

        when (charm) {
            Charm.HEALING_LEAF -> useHealingLeaf()
            Charm.CALMING_SAND -> resolveEncounterTurn { _, enc ->
                val log = "You stir the Iyanrin Ìfayabalẹ̀ into the water around you — its calm will blunt the next blow."
                enc.copy(playerShielded = true, roundLog = enc.roundLog + log, playerTurn = false)
            }
            Charm.INNER_EYE -> resolveEncounterTurn { _, enc ->
                if (enc.weakPointFound) {
                    enc.copy(playerTurn = false) // already scouted; wastes the turn harmlessly, no charge
                } else {
                    val log = "The Ojú-Inú opens — you spot a weakness in ${enc.creature.name}'s guard."
                    enc.copy(weakPointFound = true, roundLog = enc.roundLog + log, playerTurn = false)
                }
            }
            Charm.BRASS_HORN -> encounterFlee(whistleBoost = true)
            Charm.MOTHER_COWRIE -> resolveEncounterTurn { player, enc ->
                // A real charm, not a certainty: accuracy scales with oogun rather than
                // always connecting, so it no longer strictly outclasses the base Attack.
                val hit = random.nextInt(0, 100) < (COWRIE_HIT_CHANCE + player.oogun / 10)
                val damage = if (hit) random.nextInt(10, 18) + player.oogun / 10 else 0
                val newHealth = (enc.creatureHealth - damage).coerceAtLeast(0)
                val log = if (hit) {
                    "You invoke ${charm.displayName} — $damage damage to ${enc.creature.name}."
                } else {
                    "You invoke ${charm.displayName}, but the ward slips off ${enc.creature.name} harmlessly."
                }
                enc.copy(creatureHealth = newHealth, roundLog = enc.roundLog + log, playerTurn = false)
            }
            Charm.ADEORUN_TOKEN -> Unit // lore item; not usable in combat, filtered out of the UI
        }
    }

    /** Deducts a charm's oogun cost from the current player state immediately. */
    private fun spendOogun(amount: Int) {
        val current = _state.value
        _state.value = current.copy(player = current.player.clampedCopy(oogun = current.player.oogun - amount))
    }

    private fun useHealingLeaf() {
        val current = _state.value
        val encounter = current.activeEncounter ?: return
        if (!encounter.playerTurn || Charm.HEALING_LEAF !in current.player.charms) return

        val healed = random.nextInt(20, 31)
        val player = current.player
            .clampedCopy(ilera = current.player.ilera + healed)
            .copy(charms = current.player.charms - Charm.HEALING_LEAF)
        val log = "You chew the Ewé Ìwòsàn — it's spent now, but you feel $healed points steadier."

        _state.value = current.copy(
            player = player,
            activeEncounter = encounter.copy(roundLog = encounter.roundLog + log, playerTurn = false)
        )
        enemyTurn()
    }

    fun encounterFlee(whistleBoost: Boolean = false) {
        val current = _state.value
        val encounter = current.activeEncounter ?: return
        if (!encounter.playerTurn) return
        val baseChance = if (whistleBoost) 70 else 30
        val success = random.nextInt(0, 100) < (baseChance + current.player.igboya / 2)
        if (success) {
            advanceTo(encounter.creature.fleeNodeId)
        } else {
            val log = if (whistleBoost) {
                "You sound the Ìwo Idẹ, but ${encounter.creature.name} presses in anyway!"
            } else {
                "You try to flee ${encounter.creature.name}, but it blocks your escape!"
            }
            _state.value = current.copy(
                activeEncounter = encounter.copy(roundLog = encounter.roundLog + log, playerTurn = false)
            )
            enemyTurn()
        }
    }

    private fun resolveEncounterTurn(action: (Player, EncounterState) -> EncounterState) {
        val current = _state.value
        val encounter = current.activeEncounter ?: return
        if (!encounter.playerTurn) return

        val updated = action(current.player, encounter)
        _state.value = current.copy(activeEncounter = updated)

        if (updated.creatureIsDefeated) {
            advanceTo(updated.creature.victoryNodeId)
        } else {
            enemyTurn()
        }
    }

    private fun enemyTurn() {
        val current = _state.value
        val encounter = current.activeEncounter ?: return
        val creature = encounter.creature
        val name = creature.name

        val hitChance = (ENEMY_BASE_HIT_CHANCE - current.player.oogun / 10).coerceAtLeast(ENEMY_MIN_HIT_CHANCE)
        val hit = random.nextInt(0, 100) < hitChance
        val isCrit = hit && random.nextInt(0, 100) < ENEMY_CRIT_CHANCE
        val rawBase = if (hit) random.nextInt(creature.attackPower - 5, creature.attackPower + 6).coerceAtLeast(1) else 0
        val rawDamage = if (isCrit) rawBase * 2 else rawBase
        val damage = if (encounter.playerShielded) (rawDamage / 2) else rawDamage
        val newIlera = (current.player.ilera - damage).coerceAtLeast(0)
        val log = when {
            isCrit && encounter.playerShielded -> "$name finds a gap in your guard — even softened by the calming sand, that's $damage damage!"
            isCrit -> pick(
                "$name finds an opening and hits hard — $damage damage!",
                "That one really lands. $damage damage from $name."
            )
            hit && encounter.playerShielded -> "$name strikes, but the calming sand's stillness softens it to $damage damage."
            hit -> pick(
                "$name strikes back for $damage damage.",
                "$name catches you off guard — $damage damage.",
                "You take $damage damage from $name."
            )
            else -> pick(
                "$name's attack misses you.",
                "You sidestep $name's attack.",
                "$name overreaches and misses entirely."
            )
        }

        val newPlayer = current.player.copy(ilera = newIlera)
        _state.value = current.copy(
            player = newPlayer,
            activeEncounter = encounter.copy(
                roundLog = encounter.roundLog + log,
                playerTurn = true,
                playerShielded = false
            )
        )

        if (newIlera <= 0) {
            advanceTo(creature.defeatNodeId)
        }
    }

    /** Picks one line at random from a small set of flavor-text variants for combat logs. */
    private fun pick(vararg lines: String): String = lines[random.nextInt(lines.size)]

    private fun advanceTo(nodeId: String) {
        val current = _state.value
        val node = StoryData.nodes.getValue(nodeId)
        var player = current.player.copy(
            currentNodeId = node.id,
            visitedNodes = current.player.visitedNodes + node.id
        )
        node.grantsCharm?.let { charm ->
            if (charm !in player.charms) player = player.copy(charms = player.charms + charm)
        }
        _state.value = current.copy(
            player = player,
            currentNode = node,
            activeEncounter = null,
            screen = if (node.isEnding) Screen.ENDING else Screen.STORY
        )
        if (node.isEnding) clearSave() else saveGame()
    }

    private fun applyEffect(player: Player, effect: StatEffect): Player = when (effect.stat) {
        Stat.IGBOYA -> player.clampedCopy(igboya = player.igboya + effect.delta)
        Stat.OOGUN -> player.clampedCopy(oogun = player.oogun + effect.delta)
        Stat.ILERA -> player.clampedCopy(ilera = player.ilera + effect.delta)
    }

    fun toggleInventory(show: Boolean) {
        val current = _state.value
        if (current.screen == Screen.INVENTORY && !show) {
            _state.value = current.copy(screen = if (current.activeEncounter != null) Screen.ENCOUNTER else Screen.STORY)
        } else if (show) {
            _state.value = current.copy(screen = Screen.INVENTORY)
        }
    }
}
