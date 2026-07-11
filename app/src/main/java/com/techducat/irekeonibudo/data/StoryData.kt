package com.techducat.irekeonibudo.data

/**
 * All story content lives here as plain data, so writers can extend the game
 * without touching UI or engine code. This is an ORIGINAL game narrative
 * inspired by the world of D.O. Fagunwa's "Ìrèké Oníbùdó" (an orphan's
 * journey from shipwreck and captivity to wisdom and a crown) — it does not
 * reproduce text from the novel or any translation.
 */
object StoryData {

    const val START_NODE = "intro_orphan"

    val creatures: Map<String, Creature> = listOf(
        Creature(
            id = "guard_shark",
            name = "Yanyan Ẹ̀ṣọ́",
            description = "A shark the length of a canoe, scaled in dull bronze, set to guard " +
                "the water-gate of Arogidigba's sunken court.",
            maxHealth = 42,
            attackPower = 12,
            victoryNodeId = "after_shark_victory",
            defeatNodeId = "death_shark",
            fleeNodeId = "after_shark_flee"
        ),
        Creature(
            id = "arogidigba",
            name = "Arogidigba",
            description = "Queen of the deep water, half a woman's grace and half something " +
                "far older — long nails, a moon-round face, and a hunger dressed up as " +
                "hospitality.",
            maxHealth = 60,
            attackPower = 15,
            victoryNodeId = "after_arogidigba_victory",
            defeatNodeId = "death_arogidigba",
            fleeNodeId = "after_arogidigba_flee"
        ),
        Creature(
            id = "flying_python",
            name = "The Flying Ejò",
            description = "A python with wings of torn cloud, wide enough to cast a shadow over " +
                "the whole market square of Àlùpàyìdá. It has come for the tribute it was promised.",
            maxHealth = 58,
            attackPower = 14,
            victoryNodeId = "after_python_victory",
            defeatNodeId = "death_python",
            fleeNodeId = "after_python_flee"
        )
    ).associateBy { it.id }

    val nodes: Map<String, StoryNode> = listOf(

        // ==================== ACT I — THE FLOOD ====================

        StoryNode(
            id = "intro_orphan",
            title = "A House With No Elders",
            scene = SceneType.VILLAGE,
            text = "You are Ìrèké, and both your parents are three years dead. The relatives " +
                "who took the family's goats never took you in, and the town you grew up in " +
                "has gone to something worse than neglect: men steal from each other in " +
                "daylight now, and the ones who should keep order are the ones doing the " +
                "stealing. You own a single cowrie shell from your mother's waist-beads and " +
                "the clothes on your back.\n\n" +
                "Word reaches the market that a trading ship is loading at the river landing " +
                "before the rains close the water for the season. It is the only door out of " +
                "this town you can see.",
            choices = listOf(
                Choice(
                    text = "Run for the landing now, before the ship fills.",
                    nextNodeId = "call_to_sea",
                    effects = listOf(StatEffect(Stat.IGBOYA, 5))
                ),
                Choice(
                    text = "First visit the old diviner at the edge of town for a blessing.",
                    nextNodeId = "shrine_blessing"
                )
            )
        ),

        StoryNode(
            id = "shrine_blessing",
            title = "The Diviner's Warning",
            scene = SceneType.VILLAGE,
            text = "The old woman who reads palm-nuts for the town looks at you a long moment " +
                "before she throws them. \"An orphan going down to open water,\" she says, not " +
                "as a question. \"The water will ask what you're worth before it lets you " +
                "cross. Answer it with panic, and it keeps you.\" She folds a pinch of sand " +
                "from her shrine floor into a twist of cloth and presses it into your hand.",
            grantsCharm = Charm.CALMING_SAND,
            choices = listOf(
                Choice(
                    text = "Thank her and run for the landing.",
                    nextNodeId = "call_to_sea",
                    effects = listOf(StatEffect(Stat.OOGUN, 5))
                )
            )
        ),

        StoryNode(
            id = "call_to_sea",
            title = "Storm on the Water",
            scene = SceneType.RIVER,
            text = "You make the ship. Two days out, the sky the color of a bruise closes over " +
                "the water and the storm that follows does not behave like weather — it " +
                "behaves like something looking for someone. The hull splits along an old " +
                "seam. Cargo, crew, and you go into the water together.",
            choices = listOf(
                Choice(
                    text = "Strike out swimming for whatever's darkest on the horizon.",
                    nextNodeId = "sea_path_direct",
                    effects = listOf(StatEffect(Stat.IGBOYA, 3))
                ),
                Choice(
                    text = "Cling to a spar and let the current choose for you, watching it closely.",
                    nextNodeId = "sea_path_careful",
                    effects = listOf(StatEffect(Stat.OOGUN, 3))
                )
            )
        ),

        StoryNode(
            id = "sea_path_direct",
            title = "Down Into Quiet Water",
            scene = SceneType.RIVER,
            text = "You swim until your arms burn, and the storm above fades to a memory of " +
                "noise. The water beneath you goes very still, very clear, and very deep. " +
                "Something long and bronze-scaled uncoils from a wreck below and rises to " +
                "meet you.",
            choices = listOf(
                Choice(text = "Face whatever's coming up.", nextNodeId = "encounter_shark")
            )
        ),

        StoryNode(
            id = "sea_path_careful",
            title = "Reading the Current",
            scene = SceneType.RIVER,
            text = "You watch the water instead of fighting it, and the current teaches you " +
                "something: it runs in two directions at once, a fast cold pull toward a " +
                "wreck below, and a slower warm eddy curling off toward a shape you can't " +
                "quite make out — something old, and shelled, and patient, floating just " +
                "beneath the surface nearby.",
            choices = listOf(
                Choice(
                    text = "Let the cold pull take you toward the wreck.",
                    nextNodeId = "encounter_shark"
                ),
                Choice(
                    text = "Swim for the strange shelled shape instead.",
                    nextNodeId = "meeting_ijinle",
                    effects = listOf(StatEffect(Stat.OOGUN, 2))
                )
            )
        ),

        // --- Guard-shark encounter ---
        StoryNode(
            id = "encounter_shark",
            title = "Yanyan Ẹ̀ṣọ́ Rises",
            scene = SceneType.RIVER,
            text = "The shark is the length of a canoe and its scales don't shine like a fish's " +
                "should — they clink, faintly, like beaten metal. \"Small meat,\" it says, in a " +
                "voice like water forced through a narrow pipe, \"the queen doesn't feed " +
                "herself.\"",
            encounterId = "guard_shark"
        ),

        StoryNode(
            id = "after_shark_victory",
            title = "Yanyan Ẹ̀ṣọ́ Sinks",
            scene = SceneType.RIVER,
            text = "The great shark rolls belly-up and drifts down out of sight, trailing a " +
                "few bronze scales that catch the last of the light. One scale has been " +
                "hammered flat and rolled into a small horn. You take it before the current " +
                "does.",
            grantsCharm = Charm.BRASS_HORN,
            choices = listOf(
                Choice(text = "Let the current carry you onward.", nextNodeId = "meeting_ijinle")
            )
        ),

        StoryNode(
            id = "after_shark_flee",
            title = "Slipping the Current",
            scene = SceneType.RIVER,
            text = "You twist away from Yanyan Ẹ̀ṣọ́'s next pass and let a side-current drag " +
                "you sideways, lungs screaming, until its shape is lost in the murk behind " +
                "you.",
            choices = listOf(
                Choice(
                    text = "Keep swimming and hope for air.",
                    nextNodeId = "meeting_ijinle",
                    effects = listOf(StatEffect(Stat.IGBOYA, -5))
                )
            )
        ),

        StoryNode(
            id = "death_shark",
            title = "Taken Under",
            scene = SceneType.DEATH,
            text = "Yanyan Ẹ̀ṣọ́ does not miss twice. The last thing you see is bronze, and " +
                "then the surface, very far above, and then nothing.",
            isEnding = true
        ),

        // --- Ijinle, the turtle spirit ---
        StoryNode(
            id = "meeting_ijinle",
            title = "Ìjìnlẹ̀, the Old Depth",
            scene = SceneType.RIVER,
            text = "The shelled shape is a turtle the size of a market stall, older than the " +
                "wreck it rests beside. \"Ìjìnlẹ̀,\" it says, by way of introduction. \"Depth. " +
                "Most drowning men thrash past me shouting for air. You noticed me instead, " +
                "so ask me something.\"",
            choices = listOf(
                Choice(
                    text = "\"What waits for me if I keep going down?\"",
                    nextNodeId = "ijinle_arogidigba_answer"
                ),
                Choice(
                    text = "\"I've heard a winged serpent troubles a town called Àlùpàyìdá — is that true?\"",
                    nextNodeId = "ijinle_python_answer",
                    requirement = Requirement(Stat.OOGUN, 45)
                ),
                Choice(
                    text = "Bow and let the current take you onward without asking.",
                    nextNodeId = "arogidigba_court"
                ),
                Choice(
                    text = "\"There's a dark grotto in that wreck behind you — what's inside?\"",
                    nextNodeId = "grotto_entrance"
                )
            )
        ),

        // --- Grotto side-quest (optional) ---
        StoryNode(
            id = "grotto_entrance",
            title = "The Wreck's Dark Hold",
            scene = SceneType.CAVE,
            text = "You double back to the broken ship and pull yourself into its flooded " +
                "hold. It's black as a shut eye in here, and the water tastes of rust and " +
                "something that was never cargo.",
            choices = listOf(
                Choice(
                    text = "Use the Ojú-Inú to see past the dark.",
                    nextNodeId = "grotto_treasure",
                    requiresCharm = Charm.INNER_EYE
                ),
                Choice(
                    text = "Feel along the ribs of the hull by touch alone.",
                    nextNodeId = "grotto_risk",
                    effects = listOf(StatEffect(Stat.IGBOYA, 5))
                ),
                Choice(
                    text = "It isn't worth drowning for — return to Ìjìnlẹ̀.",
                    nextNodeId = "arogidigba_court"
                )
            )
        ),

        StoryNode(
            id = "grotto_treasure",
            title = "What the Hold Was Hiding",
            scene = SceneType.CAVE,
            text = "With the Ojú-Inú open, the shifting shadows resolve into ordinary broken " +
                "crates, nothing more — the dark only ever wanted to be taken at its word. " +
                "Wedged behind a fallen beam you find a carved token, warm despite the cold " +
                "water, stamped with a mark you don't recognize but somehow trust.",
            grantsCharm = Charm.ADEORUN_TOKEN,
            choices = listOf(
                Choice(
                    text = "Pocket it and swim on toward the deep court.",
                    nextNodeId = "arogidigba_court",
                    effects = listOf(StatEffect(Stat.OOGUN, 3))
                )
            )
        ),

        StoryNode(
            id = "grotto_risk",
            title = "Feeling Blind in the Wreck",
            scene = SceneType.CAVE,
            text = "Without the Ojú-Inú, the dark plays tricks on you — a rope brushes your " +
                "leg and your heart nearly stops before you realize it's only rigging. You " +
                "come up out of the hold scraped, short of breath, and short of nerve, but " +
                "your hand closes on a small carved token all the same.",
            grantsCharm = Charm.ADEORUN_TOKEN,
            choices = listOf(
                Choice(
                    text = "Pocket it and swim on, favoring your scraped arm.",
                    nextNodeId = "arogidigba_court",
                    effects = listOf(StatEffect(Stat.ILERA, -15))
                )
            )
        ),

        StoryNode(
            id = "ijinle_arogidigba_answer",
            title = "Ìjìnlẹ̀'s Counsel",
            scene = SceneType.RIVER,
            text = "\"Arogidigba,\" Ìjìnlẹ̀ says, and the water seems to go a shade colder at " +
                "the name. \"Queen of everything down here. She'll set you tasks built to be " +
                "failed, because a failed task is a fed table. Your own eyes will lie to you " +
                "in her court — you'll need to borrow better ones.\" It blinks, slow as a " +
                "closing door, and something like a bead of dark glass surfaces from beneath " +
                "its shell into your palm: the Ojú-Inú, which sees past what things pretend " +
                "to be.",
            grantsCharm = Charm.INNER_EYE,
            choices = listOf(
                Choice(
                    text = "Thank Ìjìnlẹ̀ and swim on toward the court.",
                    nextNodeId = "arogidigba_court",
                    effects = listOf(StatEffect(Stat.OOGUN, 4))
                ),
                Choice(
                    text = "Before you go — that wreck behind you. Look inside with the Ojú-Inú now open.",
                    nextNodeId = "grotto_entrance"
                )
            )
        ),

        StoryNode(
            id = "ijinle_python_answer",
            title = "Ìjìnlẹ̀'s Warning",
            scene = SceneType.RIVER,
            text = "\"True, and old news to the fish,\" Ìjìnlẹ̀ says. \"Àlùpàyìdá pays a girl to " +
                "a flying serpent every season it comes hungry, and calls it tribute instead " +
                "of what it is. If you ever stand before that serpent, know that its scales " +
                "hide a soft seam along the throat — a gift, not that they deserve one, from " +
                "an old debt of mine.\" The words settle into your memory like something " +
                "you'll need later.",
            choices = listOf(
                Choice(
                    text = "Thank Ìjìnlẹ̀ and swim on toward the deep court.",
                    nextNodeId = "arogidigba_court",
                    setsFlag = "knows_python_weakness",
                    effects = listOf(StatEffect(Stat.OOGUN, 3))
                )
            )
        ),

        // ==================== ACT I — AROGIDIGBA'S COURT ====================

        StoryNode(
            id = "arogidigba_court",
            title = "The Sunken Court",
            scene = SceneType.SPIRIT_COURT,
            text = "The water opens onto a palace built from coral and old ship-bones, lit by " +
                "a cold blue glow that comes from nowhere in particular. Arogidigba receives " +
                "you on a throne of fused anchor-chain: a woman's face, a fish's tail, and " +
                "nails the length of fingers. \"A guest,\" she says, delighted. \"Or a guest's " +
                "worth of dinner. Prove which, and I'll decide.\"",
            choices = listOf(
                Choice(
                    text = "Accept whatever task she sets, plainly.",
                    nextNodeId = "arogidigba_task",
                    effects = listOf(StatEffect(Stat.IGBOYA, 3))
                ),
                Choice(
                    text = "Use the Ojú-Inú to look past her hospitality first.",
                    nextNodeId = "arogidigba_seen_through",
                    requiresCharm = Charm.INNER_EYE
                )
            )
        ),

        StoryNode(
            id = "arogidigba_seen_through",
            title = "The Smile Underneath",
            scene = SceneType.SPIRIT_COURT,
            text = "Through the Ojú-Inú, Arogidigba's warm welcome sits over something " +
                "grinding and hungry, thin as a coat of paint. The tasks she is about to set " +
                "you were never meant to be finished — they're meant to be enjoyed, slowly, " +
                "from her side of the table. Knowing that doesn't make them easier. It just " +
                "means you won't be surprised.",
            choices = listOf(
                Choice(
                    text = "Accept the task anyway, eyes open this time.",
                    nextNodeId = "arogidigba_task",
                    setsFlag = "saw_through_arogidigba",
                    effects = listOf(StatEffect(Stat.OOGUN, 5))
                )
            )
        ),

        StoryNode(
            id = "arogidigba_task",
            title = "An Impossible Task",
            scene = SceneType.SPIRIT_COURT,
            text = "\"Sort every grain of sand in my garden by the hour it fell,\" Arogidigba " +
                "says, sweet as spoiled fruit, \"and I'll set you free with my blessing. Fail, " +
                "and you'll do it forever, which is a kind of freedom too, if you squint.\" " +
                "You kneel at the garden's edge, and the sand is endless, and your hands are " +
                "only hands — until a voice you haven't heard in three years says your name " +
                "from just behind your shoulder.",
            choices = listOf(
                Choice(
                    text = "Turn toward your mother's voice.",
                    nextNodeId = "mother_intervenes"
                )
            )
        ),

        StoryNode(
            id = "mother_intervenes",
            title = "Your Mother's Hands",
            scene = SceneType.SPIRIT_COURT,
            text = "She kneels beside you in the sand exactly as you remember her, and her " +
                "hands move through the grains faster than sight, sorting hours out of chaos " +
                "the way she once sorted grain from chaff at the family's threshing floor. " +
                "\"You were never meant to do this alone,\" she says. \"Nothing worth doing " +
                "is.\" The task is finished before Arogidigba's smile has time to change, and " +
                "when you look up, her smile has changed anyway — into something with teeth " +
                "in it.",
            choices = listOf(
                Choice(text = "Stand and face what comes next.", nextNodeId = "arogidigba_furious")
            )
        ),

        StoryNode(
            id = "arogidigba_furious",
            title = "The Table Overturns",
            scene = SceneType.SPIRIT_COURT,
            text = "\"Cheating,\" Arogidigba says, and the word comes out wrong, too many " +
                "teeth behind it, \"is not the same as finishing.\" The coral walls of her " +
                "court begin to grind shut like a closing hand.",
            choices = listOf(
                Choice(
                    text = "Offer the cowrie shell — your mother's own token — back to her spirit, and stand your ground.",
                    nextNodeId = "peaceful_pass_sea",
                    requiresCharm = Charm.MOTHER_COWRIE,
                    requiresFlag = "saw_through_arogidigba"
                ),
                Choice(
                    text = "There's no talking your way out of this — meet her head-on.",
                    nextNodeId = "encounter_arogidigba"
                )
            )
        ),

        StoryNode(
            id = "peaceful_pass_sea",
            title = "A Debt Repaid",
            scene = SceneType.SPIRIT_COURT,
            text = "You hold up the last thing you own of your mother and, instead of " +
                "fighting, simply refuse to be afraid of her anymore. Something in " +
                "Arogidigba's ancient, hungry patience actually falters — she has eaten " +
                "fear for so long she doesn't quite know what to do with its absence. " +
                "\"Go, then,\" she says, and it sounds almost like she means it. \"Small " +
                "meat with no fear left in it isn't worth the trouble.\"",
            choices = listOf(
                Choice(
                    text = "Rise to the surface without looking back.",
                    nextNodeId = "washed_ashore",
                    setsFlag = "escaped_arogidigba_unharmed",
                    effects = listOf(StatEffect(Stat.OOGUN, 10))
                )
            )
        ),

        StoryNode(
            id = "encounter_arogidigba",
            title = "The Queen Shows Her Teeth",
            scene = SceneType.SPIRIT_COURT,
            text = "Arogidigba's hospitality drops away like a discarded shell. What's " +
                "underneath is old, and fast, and has never once lost this argument before.",
            encounterId = "arogidigba"
        ),

        StoryNode(
            id = "after_arogidigba_victory",
            title = "The Court Comes Apart",
            scene = SceneType.SPIRIT_COURT,
            text = "Arogidigba folds back into the dark water she rose from, and her ruined " +
                "court begins to dissolve like sugar. In what was her garden, a broad, warm " +
                "leaf grows untouched by the wreckage — you take it before the current does.",
            grantsCharm = Charm.HEALING_LEAF,
            choices = listOf(
                Choice(text = "Kick for the surface with everything you have left.", nextNodeId = "washed_ashore")
            )
        ),

        StoryNode(
            id = "after_arogidigba_flee",
            title = "Out Through the Coral",
            scene = SceneType.SPIRIT_COURT,
            text = "You break for a gap in the collapsing coral wall and don't look back " +
                "until your lungs force you to the surface, gasping, half-convinced " +
                "something is still following.",
            choices = listOf(
                Choice(
                    text = "Swim for the nearest shore.",
                    nextNodeId = "washed_ashore",
                    effects = listOf(StatEffect(Stat.IGBOYA, -5))
                )
            )
        ),

        StoryNode(
            id = "death_arogidigba",
            title = "A Guest's Worth of Dinner",
            scene = SceneType.DEATH,
            text = "Arogidigba was right, in the end, about which kind of guest you were. " +
                "Her table is set again tomorrow, for someone else.",
            isEnding = true
        ),

        // ==================== ACT II — ÀLÙPÀYÌDÁ ====================

        StoryNode(
            id = "washed_ashore",
            title = "Washed Up at Àlùpàyìdá",
            scene = SceneType.VILLAGE,
            text = "The tide sets you down, half-drowned and shivering, on the mud-flats " +
                "below a walled town. You're pulled upright by market women who take one " +
                "look at your salt-crusted clothes and go quiet with pity rather than " +
                "questions — pity, you'll learn within the hour, is the town's normal mood " +
                "today. Drums are sounding from the palace, slow and funereal, though nobody " +
                "has died yet.",
            choices = listOf(
                Choice(text = "Ask what the drums are for.", nextNodeId = "learn_of_python")
            )
        ),

        StoryNode(
            id = "learn_of_python",
            title = "The Season's Tribute",
            scene = SceneType.VILLAGE,
            text = "A winged python has troubled Àlùpàyìdá for three seasons, and today is " +
                "payment day: the king's own daughter, Ifepade, is to be left at the shrine " +
                "gate before sundown, as the town's elders decided was cheaper, in the end, " +
                "than fighting it. Nobody meets your eyes while they tell you this. Somewhere " +
                "in the palace, you'd guess, someone is dressing a girl for a funeral that " +
                "hasn't been called that yet.",
            choices = listOf(
                Choice(
                    text = "Go to the shrine gate before sundown.",
                    nextNodeId = "shrine_gate",
                    effects = listOf(StatEffect(Stat.IGBOYA, 3))
                )
            )
        ),

        StoryNode(
            id = "shrine_gate",
            title = "Ifepade, Waiting",
            scene = SceneType.VILLAGE,
            text = "She is sitting very straight on the shrine steps in ceremonial white, " +
                "and the only thing that gives her away is how tightly her hands are folded " +
                "in her lap. \"You're new,\" she says, when she notices you. \"Everyone else " +
                "in this town has had three seasons to learn not to stand near me today.\"",
            choices = listOf(
                Choice(
                    text = "\"I'm not leaving. Whatever comes for you, it goes through me first.\"",
                    nextNodeId = "python_approach",
                    effects = listOf(StatEffect(Stat.IGBOYA, 5))
                ),
                Choice(
                    text = "Say nothing — just sit down on the steps beside her and wait.",
                    nextNodeId = "python_approach",
                    effects = listOf(StatEffect(Stat.OOGUN, 5))
                )
            )
        ),

        StoryNode(
            id = "python_approach",
            title = "A Shadow Crosses the Square",
            scene = SceneType.VILLAGE,
            text = "The sky darkens the way it does before rain, except it isn't rain — it's " +
                "wings, and beneath them a python long enough to have swallowed the shadow of " +
                "the whole market square. It lands at the shrine gate exactly on time, coiling " +
                "down toward the steps like it's done this three times before, because it has.",
            choices = listOf(
                Choice(
                    text = "Use the Ojú-Inú to find its weak point before it strikes.",
                    nextNodeId = "python_weak_point",
                    requiresCharm = Charm.INNER_EYE
                ),
                Choice(
                    text = "You already know the soft seam along its throat — strike there first.",
                    nextNodeId = "encounter_python",
                    requiresFlag = "knows_python_weakness"
                ),
                Choice(
                    text = "There's no time to be clever. Put yourself between it and Ifepade.",
                    nextNodeId = "encounter_python"
                )
            )
        ),

        StoryNode(
            id = "python_weak_point",
            title = "A Seam in the Scales",
            scene = SceneType.VILLAGE,
            text = "Through the Ojú-Inú, the python's armor of scales isn't uniform at all — " +
                "there's a narrow, paler seam along its throat where the plates never fully " +
                "closed. You'll be seeing double for a moment after you let the sight go, " +
                "but you know exactly where to put your first strike now.",
            choices = listOf(
                Choice(
                    text = "Attack before the moment passes.",
                    nextNodeId = "encounter_python",
                    setsFlag = "spotted_python_weakness"
                )
            )
        ),

        // --- Python encounter ---
        StoryNode(
            id = "encounter_python",
            title = "The Flying Ejò Descends",
            scene = SceneType.VILLAGE,
            text = "It doesn't hiss a warning or wait for tribute to be handed over politely " +
                "— three seasons of an unchallenged meal have made it lazy about ceremony. It " +
                "simply comes for whoever is standing closest to the girl in white.",
            encounterId = "flying_python"
        ),

        StoryNode(
            id = "after_python_victory",
            title = "The Shadow Lifts",
            scene = SceneType.VICTORY,
            text = "The great serpent comes apart in a last thrash of torn-cloud wings and " +
                "goes still across the shrine steps, and for the first time in three seasons " +
                "the drums stop of their own accord instead of on schedule. Ifepade is on her " +
                "feet before you are, staring at you like she's recalculating something.",
            choices = listOf(
                Choice(text = "Meet her eyes.", nextNodeId = "meeting_ifepade")
            )
        ),

        StoryNode(
            id = "after_python_flee",
            title = "A Costly Retreat",
            scene = SceneType.VILLAGE,
            text = "You pull back from the python's next strike and it rears up, confused for " +
                "a moment by prey that moves, before turning its attention back to the steps. " +
                "You'll need to try again, and there's less daylight left to do it in.",
            choices = listOf(
                Choice(
                    text = "Go back in.",
                    nextNodeId = "encounter_python",
                    effects = listOf(StatEffect(Stat.IGBOYA, -5))
                )
            )
        ),

        StoryNode(
            id = "death_python",
            title = "The Season's Tribute, Paid Twice",
            scene = SceneType.DEATH,
            text = "The python takes its meal a day early this season. The drums in Àlùpàyìdá " +
                "will sound again next year, on schedule, as if this were normal, because to " +
                "the town, by now, it is.",
            isEnding = true
        ),

        // --- Romance & the court's jealousy ---
        StoryNode(
            id = "meeting_ifepade",
            title = "What Comes After Surviving",
            scene = SceneType.VILLAGE,
            text = "Ifepade does not thank you the way a rescued princess is supposed to in " +
                "the stories the town will tell about today. Instead she asks your name, then " +
                "asks it again like she's making sure she'll remember it right, then laughs at " +
                "herself for how much that seems to matter all of a sudden. The king, watching " +
                "from the palace steps, has already begun making plans that have nothing to " +
                "do with gratitude.",
            choices = listOf(
                Choice(text = "Follow her up to the palace to meet her father properly.", nextNodeId = "palace_welcome")
            )
        ),

        StoryNode(
            id = "palace_welcome",
            title = "A Guest of the Palace",
            scene = SceneType.SPIRIT_COURT,
            text = "The king embraces you as a son before the whole court and means it, for " +
                "now, which is its own kind of danger in a palace this small. Weeks pass. You " +
                "and Ifepade fall into something neither of you asked permission for, and the " +
                "court — as courts do — starts asking who exactly this penniless orphan thinks " +
                "he is to sit this close to the throne.",
            choices = listOf(
                Choice(text = "Let the weeks pass and see what comes of it.", nextNodeId = "court_rumor")
            )
        ),

        StoryNode(
            id = "court_rumor",
            title = "A Rumor With Teeth",
            scene = SceneType.SPIRIT_COURT,
            text = "It starts small — a whisper that the stranger who killed the serpent has " +
                "been seen leaving the queens' quarters at strange hours — and grows the way " +
                "rumors do when enough people would benefit from them being true. By the time " +
                "it reaches the king, it has details it never earned. He summons you to the " +
                "throne room, and his face is the face of a man who wants, badly, to be told " +
                "he's wrong.",
            choices = listOf(
                Choice(
                    text = "Deny it plainly and ask him to name your accuser.",
                    nextNodeId = "confront_accuser",
                    requirement = Requirement(Stat.IGBOYA, 55)
                ),
                Choice(
                    text = "Say nothing in your own defense and simply leave the palace that night.",
                    nextNodeId = "flee_alupayida"
                )
            )
        ),

        StoryNode(
            id = "confront_accuser",
            title = "Naming Names",
            scene = SceneType.SPIRIT_COURT,
            text = "You hold your ground long enough that the rumor's actual source — a " +
                "junior court official who'd hoped to marry Ifepade to his own nephew — loses " +
                "his nerve first and admits, badly, to inventing most of it. The king's anger " +
                "doesn't vanish so much as run out of a place to land. He orders you flogged " +
                "anyway, for the trouble of the accusation existing at all, which in this " +
                "palace passes for mercy.",
            choices = listOf(
                Choice(
                    text = "Take the punishment and stay.",
                    nextNodeId = "reconciliation",
                    effects = listOf(StatEffect(Stat.ILERA, -20))
                ),
                Choice(
                    text = "This is not a place that will ever fully forgive you — leave anyway.",
                    nextNodeId = "flee_alupayida"
                )
            )
        ),

        StoryNode(
            id = "flee_alupayida",
            title = "Gone Before Dawn",
            scene = SceneType.VILLAGE,
            text = "You leave the way you arrived — with nothing but the clothes on your " +
                "back and, this time, a town's worth of gossip trailing you out the gate. " +
                "Ifepade wakes to an empty room and, rather than accept the story the court " +
                "gives her, starts asking questions of her own.",
            choices = listOf(
                Choice(text = "Keep walking and don't look back.", nextNodeId = "ifepade_search")
            )
        ),

        StoryNode(
            id = "ifepade_search",
            title = "Found Again",
            scene = SceneType.VILLAGE,
            text = "It takes her the better part of a season, but Ifepade is not a woman who " +
                "accepts a mystery lying down — she has messengers describe you at every " +
                "market between here and the coast until one of them finally says yes, that " +
                "man, right there, and there you are. \"You could have just told me you were " +
                "leaving,\" she says, more exhausted than angry.",
            choices = listOf(
                Choice(text = "Return to Àlùpàyìdá together.", nextNodeId = "reconciliation")
            )
        ),

        StoryNode(
            id = "reconciliation",
            title = "An Uneasy Peace",
            scene = SceneType.SPIRIT_COURT,
            text = "Whatever the court believes now, the king's anger has genuinely gone out " +
                "of him by the time you return — grief has taken its place instead. Ifepade " +
                "has fallen ill with a wasting sickness no palace herbalist can name, and the " +
                "king, watching his only remaining reason for living fade by the week, has run " +
                "out of pride to spend on old rumors.",
            choices = listOf(
                Choice(text = "Ask the king what can still be done.", nextNodeId = "king_sends_quest")
            )
        ),

        StoryNode(
            id = "king_sends_quest",
            title = "The Only Name Left",
            scene = SceneType.SPIRIT_COURT,
            text = "\"There is a wise one,\" the king says, \"far past the Mountain of " +
                "Trials, who is said to know a cure for anything, including whatever is " +
                "wrong with this whole broken household of mine. His name is Itanforiti. Go " +
                "to him. Bring back whatever he'll give you — a cure, or failing that, enough " +
                "sense to run this kingdom better than I have.\"",
            choices = listOf(
                Choice(
                    text = "Set out for the mountain at once.",
                    nextNodeId = "mountain_path",
                    effects = listOf(StatEffect(Stat.IGBOYA, 5))
                )
            )
        ),

        // ==================== ACT III — THE MOUNTAIN OF TRIALS ====================

        StoryNode(
            id = "mountain_path",
            title = "The Mountain of Trials",
            scene = SceneType.FOREST_PATH,
            text = "The road past Àlùpàyìdá climbs fast and then keeps climbing, through air " +
                "that thins and trees that stop bothering to grow straight. Somewhere ahead, " +
                "a voice — small, dry, entirely too pleased with itself — calls out that it " +
                "knows a shortcut, for a price.",
            choices = listOf(
                Choice(
                    text = "Follow the voice and hear its price.",
                    nextNodeId = "ijapa_riddle"
                ),
                Choice(
                    text = "Ignore it and keep to the long way up.",
                    nextNodeId = "town_of_men"
                )
            )
        ),

        StoryNode(
            id = "ijapa_riddle",
            title = "Ìjàpá's Toll",
            scene = SceneType.FOREST_PATH,
            text = "The tortoise Ìjàpá is sunning himself on a boundary stone, entirely too " +
                "large for his shell to explain. \"One question, hunter's-luck stranger,\" he " +
                "says. \"Answer right, and I fold this whole mountain in half for you. Answer " +
                "wrong, and you walk it the slow way, which frankly you'd have done anyway.\" " +
                "He clears his throat theatrically. \"I am carried, but never walk. I open " +
                "doors I cannot see. A hunter's son gave you one. What am I?\"",
            choices = listOf(
                Choice(text = "\"A key.\"", nextNodeId = "ijapa_success"),
                Choice(text = "\"A blessing.\"", nextNodeId = "ijapa_fail"),
                Choice(text = "\"I don't know — I yield.\"", nextNodeId = "town_of_men")
            )
        ),

        StoryNode(
            id = "ijapa_success",
            title = "Ìjàpá Keeps His Word, For Once",
            scene = SceneType.FOREST_PATH,
            text = "\"Correct, and unfairly quick about it,\" Ìjàpá grumbles, already sounding " +
                "like he regrets the wager. He thumps the boundary stone twice with one small " +
                "foot, and the path ahead folds the way a fan folds, cutting clean past both " +
                "the town of men and the town of women entirely and setting you down within " +
                "sight of a dark opening in the mountainside.",
            choices = listOf(
                Choice(
                    text = "Thank him and approach the opening.",
                    nextNodeId = "itanforiti_threshold",
                    setsFlag = "used_ijapa_shortcut",
                    effects = listOf(StatEffect(Stat.OOGUN, 5))
                )
            )
        ),

        StoryNode(
            id = "ijapa_fail",
            title = "Wrong Guess",
            scene = SceneType.FOREST_PATH,
            text = "\"A blessing?\" Ìjàpá looks personally wounded by the answer. \"No fold in " +
                "this mountain for you, then.\" He slides off the stone and vanishes into the " +
                "scrub, leaving you to the long way up, considerably more annoyed than you " +
                "were a minute ago.",
            choices = listOf(
                Choice(
                    text = "Keep climbing.",
                    nextNodeId = "town_of_men",
                    effects = listOf(StatEffect(Stat.IGBOYA, -3))
                )
            )
        ),

        StoryNode(
            id = "town_of_men",
            title = "The Town of Men Alone",
            scene = SceneType.VILLAGE,
            text = "Halfway up the mountain sits a town of men only, tidy and quiet and, you " +
                "gather from an old resident's careful explanation, deeply lonely — when a man " +
                "here is ready to marry, custom requires a wife be sent for from somewhere " +
                "considerably stranger than the next village over. Nobody here troubles you. " +
                "Nobody here, you get the sense, has much left to say to a stranger passing " +
                "through.",
            choices = listOf(
                Choice(text = "Pass through without lingering.", nextNodeId = "town_of_women")
            )
        ),

        StoryNode(
            id = "town_of_women",
            title = "The Town of Women Alone",
            scene = SceneType.VILLAGE,
            text = "Higher still, its mirror: a town of women only, who watch you cross their " +
                "square with the specific, appraising patience of people who have seen very " +
                "few strangers and intend to remember this one. Nobody stops you either. The " +
                "path beyond climbs on toward a dark opening in the mountainside.",
            choices = listOf(
                Choice(text = "Continue to the opening in the rock.", nextNodeId = "itanforiti_threshold")
            )
        ),

        StoryNode(
            id = "itanforiti_threshold",
            title = "A Hole in the Ground",
            scene = SceneType.CAVE,
            text = "Itanforiti's dwelling is, unpromisingly, a hole in the ground — until you " +
                "notice the hole is lit from within by a warm and steady light that no torch " +
                "makes. A voice from inside, patient as bedrock, asks who has come climbing " +
                "so far up a mountain for an old man who never advertised.",
            choices = listOf(
                Choice(
                    text = "\"Ìrèké, sent by the king of Àlùpàyìdá, for wisdom and a cure.\"",
                    nextNodeId = "itanforiti_meeting"
                ),
                Choice(
                    text = "Offer the carved token from the wreck, and say nothing else.",
                    nextNodeId = "itanforiti_token",
                    requiresCharm = Charm.ADEORUN_TOKEN
                )
            )
        ),

        StoryNode(
            id = "itanforiti_token",
            title = "A Token Recognized",
            scene = SceneType.CAVE,
            text = "The old voice goes quiet a long moment at the sight of the token. \"Adéọrun " +
                "still gives those out, then,\" it says, warmer now, almost fond. \"He doesn't " +
                "hand them to just anyone drowning in a wreck. Come in, since he apparently " +
                "vouches for you — that saves us both a great deal of testing.\"",
            choices = listOf(
                Choice(
                    text = "Step down into the light.",
                    nextNodeId = "itanforiti_meeting",
                    setsFlag = "vouched_by_adeorun",
                    effects = listOf(StatEffect(Stat.OOGUN, 8))
                )
            )
        ),

        StoryNode(
            id = "itanforiti_meeting",
            title = "Itanforiti",
            scene = SceneType.CAVE,
            text = "He has the head of an old man and, unmistakably, the feet of a cow, and " +
                "he is somehow the least strange thing you've met since the storm took your " +
                "ship. \"Sit,\" he says. \"Everyone who reaches me wants a cure for someone " +
                "else's suffering. Before I give you one, tell me honestly what you think is " +
                "actually wrong with that palace you came from.\"",
            choices = listOf(
                Choice(
                    text = "\"A king too proud to admit his court poisoned something good with rumor.\"",
                    nextNodeId = "itanforiti_pride_lesson",
                    requirement = Requirement(Stat.OOGUN, 55)
                ),
                Choice(
                    text = "\"A girl dying because nobody in that palace was ever taught to love without owning.\"",
                    nextNodeId = "itanforiti_love_lesson"
                ),
                Choice(
                    text = "\"I don't fully know — that's why I climbed a mountain to ask you instead of guessing.\"",
                    nextNodeId = "itanforiti_honesty_lesson"
                )
            )
        ),

        StoryNode(
            id = "itanforiti_pride_lesson",
            title = "On Covetousness",
            scene = SceneType.CAVE,
            text = "Itanforiti nods slowly, unsurprised. \"A man who guards his throne with " +
                "suspicion instead of love ends up ruling a room full of people just as " +
                "suspicious as he is — he trained them to it.\" He presses a small clay vial " +
                "into your hand. \"This cures the sickness. It will not cure the palace. That " +
                "part is yours to finish, if you're willing to stay and do it.\"",
            choices = listOf(
                Choice(text = "Accept the vial and start back down the mountain.", nextNodeId = "return_to_alupayida")
            )
        ),

        StoryNode(
            id = "itanforiti_love_lesson",
            title = "On Love",
            scene = SceneType.CAVE,
            text = "\"Closer,\" Itanforiti says, something like approval in his old voice. " +
                "\"Love is the only rope strong enough to hold a household together once fear " +
                "stops working — and fear always stops working eventually.\" He hands you the " +
                "vial. \"Give this to her yourself. Not a servant. The medicine only half " +
                "matters. Who hands it to her matters the other half.\"",
            choices = listOf(
                Choice(
                    text = "Accept the vial and start back down the mountain.",
                    nextNodeId = "return_to_alupayida",
                    setsFlag = "learned_of_love"
                )
            )
        ),

        StoryNode(
            id = "itanforiti_honesty_lesson",
            title = "On Honesty",
            scene = SceneType.CAVE,
            text = "Itanforiti actually laughs, a dry sound like a door hinge. \"Every other " +
                "visitor I get pretends to already understand the thing they've climbed a " +
                "mountain to learn. You're the first this decade to just say you don't.\" He " +
                "hands you the vial without further test. \"That, alone, is most of the " +
                "wisdom I had to give you. The rest you'll work out on the way down.\"",
            choices = listOf(
                Choice(
                    text = "Accept the vial and start back down the mountain.",
                    nextNodeId = "return_to_alupayida",
                    setsFlag = "learned_of_honesty"
                )
            )
        ),

        // ==================== ENDING ====================

        StoryNode(
            id = "return_to_alupayida",
            title = "Home to a Waiting Palace",
            scene = SceneType.SPIRIT_COURT,
            text = "The king meets you at the gate himself, which he has never once done for " +
                "anyone. Ifepade is carried out to see you arrive, too weak to stand but awake " +
                "enough to argue with her attendants about it. Itanforiti's vial is small " +
                "enough to fit in one closed hand.",
            choices = listOf(
                Choice(
                    text = "Give the medicine to Ifepade yourself, and tell her what Itanforiti said about love.",
                    nextNodeId = "ending_love",
                    requiresFlag = "learned_of_love"
                ),
                Choice(
                    text = "Present the medicine to the king first, as is formally proper.",
                    nextNodeId = "ending_pride_reformed",
                    requiresFlag = "learned_of_honesty"
                ),
                Choice(
                    text = "Simply hand the vial to the court physician and step back.",
                    nextNodeId = "ending_dutiful"
                ),
                Choice(
                    text = "Offer the vial along with the carved token, saying nothing of what either one cost you.",
                    nextNodeId = "ending_secret",
                    requiresCharm = Charm.ADEORUN_TOKEN,
                    requiresFlag = "vouched_by_adeorun"
                )
            )
        ),

        StoryNode(
            id = "ending_love",
            title = "What the Cure Actually Was",
            scene = SceneType.VICTORY,
            text = "Ifepade recovers within days, faster than the herbalists can quite " +
                "explain, and the king — watching his daughter laugh again at something you " +
                "said under her breath — finally stops asking whether you're worthy of her and " +
                "starts asking when the wedding will be. Years later, when the old king dies " +
                "peacefully in his sleep, Àlùpàyìdá does not hesitate over who should take the " +
                "throne. You rule a household that finally learned, the hard way, what " +
                "Itanforiti tried to tell you on a mountain: that fear rots a palace from the " +
                "inside, and love is the only rope strong enough to hold one up instead.",
            isEnding = true
        ),

        StoryNode(
            id = "ending_pride_reformed",
            title = "A King Who Finally Listened",
            scene = SceneType.VICTORY,
            text = "The king takes the vial from your hands like a man accepting a verdict, " +
                "not a gift, and for once doesn't argue with it. Ifepade recovers slowly, but " +
                "she recovers. More surprising still, the king spends his remaining years " +
                "actually trying to be the ruler Itanforiti's lesson accused him of failing to " +
                "be — and names you, publicly and without prompting, as the reason he finally " +
                "started. When he dies, the throne comes to you not by rumor or romance but by " +
                "a court that watched you both change and decided you'd earned it.",
            isEnding = true
        ),

        StoryNode(
            id = "ending_dutiful",
            title = "A Cure, Delivered Properly",
            scene = SceneType.VICTORY,
            text = "You step back and let the physicians do their work without inserting " +
                "yourself into the story of it, and Ifepade recovers all the same. It's a " +
                "quieter ending than the songs about you will eventually claim — no mountain " +
                "romance, no dramatic bedside vow — just an orphan who did the necessary thing " +
                "competently and let the credit fall where it wanted to. In time, when the " +
                "throne needs a steady hand more than a beloved one, it finds its way to you " +
                "anyway.",
            isEnding = true
        ),

        StoryNode(
            id = "ending_secret",
            title = "The Debt Nobody Named",
            scene = SceneType.VICTORY,
            text = "At the sight of the carved token, the king's face does something none of " +
                "his courtiers have ever managed to describe to each other afterward without " +
                "disagreeing about it. \"Adéọrun's mark,\" he says quietly, \"was buried with " +
                "my own grandfather's debts, and no one alive was meant to still be carrying " +
                "one of these.\" He asks nothing further. Ifepade recovers; the token " +
                "disappears back into whatever ledger it came from; and you leave the whole " +
                "audience having explained nothing at all, which — as with the forest before " +
                "it — turns out to be exactly what was owed.",
            isEnding = true
        )

    ).associateBy { it.id }
}
