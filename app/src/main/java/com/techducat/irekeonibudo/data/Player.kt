package com.techducat.irekeonibudo.data

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

enum class Charm(val displayName: String, val description: String) {
    MOTHER_COWRIE("Owó Ẹyọ Ìyá", "A single cowrie shell from your late mother's waist-beads — all you carry of her."),
    INNER_EYE("Ojú-Inú", "A sight beyond sight, taught to you by the old turtle-spirit of the deep. Sees past disguise and illusion."),
    CALMING_SAND("Iyanrin Ìfayabalẹ̀", "A pinch of sand blessed at the shrine. Stirred into troubled water, it stills a storm."),
    BRASS_HORN("Ìwo Idẹ", "A small brass horn taken from a beaten guardian. Its note unsettles lesser creatures into flight."),
    HEALING_LEAF("Ewé Ìwòsàn", "A broad, warm leaf found in Arogidigba's ruined garden. Chewed, it knits flesh and nerve alike."),
    ADEORUN_TOKEN("Àmì Adéọrun", "A carved token left by the spirit Adéọrun, warm to the touch though its meaning is not yet clear.")
}
