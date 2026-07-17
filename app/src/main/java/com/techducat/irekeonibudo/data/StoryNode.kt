package com.techducat.irekeonibudo.data

/** Which stat a requirement or effect is expressed against. */
enum class Stat { IGBOYA, OOGUN, ILERA }

/** A minimum-stat gate on a choice. The choice is hidden/disabled if not met. */
data class Requirement(val stat: Stat, val min: Int)

/** A stat delta applied when a choice is taken. */
data class StatEffect(val stat: Stat, val delta: Int)

/** The type of illustration SceneCanvas should draw for a node. */
enum class SceneType { VILLAGE, FOREST_PATH, RIVER, CAVE, SPIRIT_COURT, VICTORY, DEATH }

/**
 * One beat of the interactive story. A node is either:
 *  - narrative, with a list of [choices] the player picks from, or
 *  - an encounter, in which case [encounterId] points at a [Creature] and the
 *    game hands control to the EncounterScreen instead of rendering choices.
 */
data class StoryNode(
    val id: String,
    val title: String,
    val scene: SceneType,
    val text: String,
    val choices: List<Choice> = emptyList(),
    val encounterId: String? = null,
    val grantsCharm: Charm? = null,
    val isEnding: Boolean = false
)

data class Choice(
    val text: String,
    val nextNodeId: String,
    val requirement: Requirement? = null,
    val requiresCharm: Charm? = null,
    val effects: List<StatEffect> = emptyList(),
    val requiresFlag: String? = null,
    val setsFlag: String? = null
)

/** A creature or trial encountered on the journey, fought in real time via DuelScreen/DuelEngine. */
data class Creature(
    val id: String,
    val name: String,
    val description: String,
    val maxHealth: Int,
    val attackPower: Int,
    val victoryNodeId: String,
    val defeatNodeId: String,
    val fleeNodeId: String
)
