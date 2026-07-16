package com.techducat.irekeonibudo.data

/** Which top-level screen the game is currently showing. */
enum class Screen { TITLE, STORY, ENCOUNTER, INVENTORY, ENDING }

data class GameState(
    val screen: Screen = Screen.TITLE,
    val player: Player = Player(),
    val currentNode: StoryNode = StoryData.nodes.getValue(StoryData.START_NODE),
    val activeEncounter: EncounterState? = null,
    val log: List<String> = emptyList()
) {
    /** Choices filtered to only those the player currently meets requirements for. */
    fun availableChoices(node: StoryNode = currentNode): List<Choice> = node.choices.filter { choice ->
        val meetsStat = choice.requirement?.let { req ->
            when (req.stat) {
                Stat.IGBOYA -> player.igboya >= req.min
                Stat.OOGUN -> player.oogun >= req.min
                Stat.ILERA -> player.ilera >= req.min
            }
        } ?: true
        val meetsCharm = choice.requiresCharm?.let { it in player.charms } ?: true
        val meetsFlag = choice.requiresFlag?.let { it in player.flags } ?: true
        meetsStat && meetsCharm && meetsFlag
    }
}

/** Turn-based encounter state, mutated each round by GameViewModel. */
data class EncounterState(
    val creature: Creature,
    val creatureHealth: Int,
    val playerTurn: Boolean = true,
    val roundLog: List<String> = emptyList(),
    /** Set for one enemy turn by Òkúta Omi (Water Stone); halves the next hit taken. */
    val playerShielded: Boolean = false,
    /** Set by the Eye of Ìmọ̀dòyè; boosts every subsequent attack this fight. */
    val weakPointFound: Boolean = false
) {
    val creatureIsDefeated: Boolean get() = creatureHealth <= 0
}
