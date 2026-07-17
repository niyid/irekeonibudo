package com.techducat.irekeonibudo.data

import androidx.annotation.StringRes
import com.techducat.irekeonibudo.R

/**
 * The player's persistent state as Ìrèké, an orphaned wanderer making his way
 * in a world of tricksters, spirits, and kings.
 *
 * Three core stats drive every check in the game:
 *  - igboya  (courage)     : raw nerve, needed to face the world's monsters
 *  - oogun   (charm power) : mystical skill, spent to invoke protective/attacking charms
 *  - ilera   (health)      : life force; reaching 0 ends the run
 */
data class Player(
    val name: String = "Ìrèké",
    val igboya: Int = 50,
    val oogun: Int = 45,
    val ilera: Int = 100,
    val charms: List<Charm> = listOf(Charm.MOTHER_COWRIE),
    val currentNodeId: String = StoryData.START_NODE,
    val visitedNodes: Set<String> = emptySet(),
    val flags: Set<String> = emptySet()
) {
    val isAlive: Boolean get() = ilera > 0

    fun clampedCopy(
        igboya: Int = this.igboya,
        oogun: Int = this.oogun,
        ilera: Int = this.ilera,
        charms: List<Charm> = this.charms,
        currentNodeId: String = this.currentNodeId,
        visitedNodes: Set<String> = this.visitedNodes,
        flags: Set<String> = this.flags
    ) = copy(
        igboya = igboya.coerceIn(0, 100),
        oogun = oogun.coerceIn(0, 100),
        ilera = ilera.coerceIn(0, 100),
        charms = charms,
        currentNodeId = currentNodeId,
        visitedNodes = visitedNodes,
        flags = flags
    )
}

enum class Charm(@StringRes val displayNameRes: Int, @StringRes val descriptionRes: Int) {
    MOTHER_COWRIE(R.string.charm_mother_cowrie_name, R.string.charm_mother_cowrie_description),
    INNER_EYE(R.string.charm_inner_eye_name, R.string.charm_inner_eye_description),
    CALMING_SAND(R.string.charm_calming_sand_name, R.string.charm_calming_sand_description),
    BRASS_HORN(R.string.charm_brass_horn_name, R.string.charm_brass_horn_description),
    HEALING_LEAF(R.string.charm_healing_leaf_name, R.string.charm_healing_leaf_description),
    ADEORUN_TOKEN(R.string.charm_adeorun_token_name, R.string.charm_adeorun_token_description)
}
