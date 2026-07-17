package com.techducat.irekeonibudo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techducat.irekeonibudo.data.Charm
import com.techducat.irekeonibudo.data.Choice
import com.techducat.irekeonibudo.data.DodgeDirection
import com.techducat.irekeonibudo.data.DuelOutcome
import com.techducat.irekeonibudo.data.DuelState
import com.techducat.irekeonibudo.data.GameRepository
import com.techducat.irekeonibudo.data.GameState
import com.techducat.irekeonibudo.data.Player
import com.techducat.irekeonibudo.data.Screen
import com.techducat.irekeonibudo.data.Stat
import com.techducat.irekeonibudo.data.StatEffect
import com.techducat.irekeonibudo.data.StoryData
import com.techducat.irekeonibudo.engine.DuelEngine
import com.techducat.irekeonibudo.engine.DuelResult
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
        /** Oogun cost to invoke a charm mid-duel — mirrors DuelEngine.CHARM_OOGUN_COST. */
        const val CHARM_OOGUN_COST = DuelEngine.CHARM_OOGUN_COST
    }

    /** True if [charm] can currently be invoked (enough oogun; cooldowns/turn gates live in DuelEngine). */
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
            activeEncounter = node.encounterId?.let { encounterId -> startDuel(encounterId, loaded) }
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
            activeEncounter = if (enteringEncounter) startDuel(nextNode.encounterId!!, player) else null
        )
        when {
            nextNode.isEnding -> clearSave()
            !enteringEncounter -> saveGame()
        }
    }

    /**
     * Starts a duel, pre-scouting the weak point when the player has already spotted it in the
     * story (e.g. the python's throat-seam scene) so a prior narrative choice keeps paying off
     * in the fight itself, same as it did under the old turn-based encounter system.
     */
    private fun startDuel(encounterId: String, player: Player): DuelState {
        val duel = DuelEngine.start(StoryData.creatures.getValue(encounterId), random)
        val preScouted = encounterId == "flying_python" && "spotted_python_weakness" in player.flags
        return if (preScouted) duel.copy(weakPointFound = true) else duel
    }

    // --- Real-time duel ---
    // Every function below just delegates to DuelEngine (pure logic) and applies the
    // resulting (DuelState, Player) pair, then checks whether the duel just resolved.

    /** Called every frame from DuelScreen's game loop. */
    fun duelTick(deltaMs: Long) = applyDuel { duel, player -> DuelEngine.tick(duel, player, deltaMs, random) }

    fun duelDodge(direction: DodgeDirection) = applyDuel { duel, player ->
        DuelResult(DuelEngine.dodge(duel, direction), player)
    }

    fun duelBlockStart() = applyDuel { duel, player -> DuelResult(DuelEngine.blockStart(duel), player) }

    fun duelBlockEnd() = applyDuel { duel, player -> DuelResult(DuelEngine.blockEnd(duel), player) }

    fun duelAttack() = applyDuel { duel, player -> DuelResult(DuelEngine.attack(duel), player) }

    fun duelUseCharm(charm: Charm) = applyDuel { duel, player -> DuelEngine.useCharm(duel, player, charm, random) }

    fun duelFlee() = applyDuel { duel, player -> DuelEngine.flee(duel, player, random) }

    /** Runs an engine call against the current duel/player, commits the result, then resolves outcomes. */
    private inline fun applyDuel(action: (DuelState, Player) -> DuelResult) {
        val current = _state.value
        val duel = current.activeEncounter ?: return
        val result = action(duel, current.player)
        _state.value = current.copy(player = result.player, activeEncounter = result.state)

        when (result.state.outcome) {
            DuelOutcome.VICTORY -> advanceTo(result.state.creature.victoryNodeId)
            DuelOutcome.DEFEAT -> advanceTo(result.state.creature.defeatNodeId)
            DuelOutcome.FLED -> advanceTo(result.state.creature.fleeNodeId)
            null -> Unit
        }
    }

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
