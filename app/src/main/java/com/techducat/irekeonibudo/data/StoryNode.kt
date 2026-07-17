package com.techducat.irekeonibudo.data

import androidx.annotation.StringRes

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
 *
 * [titleRes]/[textRes] are string resource ids rather than raw text, so the
 * narrative is localizable via strings.xml without touching this data.
 */
data class StoryNode(
    val id: String,
    @StringRes val titleRes: Int,
    val scene: SceneType,
    @StringRes val textRes: Int,
    val choices: List<Choice> = emptyList(),
    val encounterId: String? = null,
    val grantsCharm: Charm? = null,
    val isEnding: Boolean = false
)

data class Choice(
    @StringRes val textRes: Int,
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
    @StringRes val nameRes: Int,
    @StringRes val descriptionRes: Int,
    val maxHealth: Int,
    val attackPower: Int,
    val victoryNodeId: String,
    val defeatNodeId: String,
    val fleeNodeId: String
)
