package com.techducat.irekeonibudo.data

import com.techducat.irekeonibudo.R

/**
 * All story content lives here as plain data, so writers can extend the game
 * without touching UI or engine code. This is an ORIGINAL game narrative
 * inspired by the world and characters of D.O. Fagunwa's Yoruba adventure
 * tradition (an orphan's journey through flood, spirit court, and trial by
 * mountain) — it does not reproduce text from any specific novel or
 * translation.
 *
 * All player-facing prose lives in strings.xml (see the "Story content"
 * section) rather than here, so the game can be localized without touching
 * this file: each node/creature/choice holds a @StringRes id, resolved to
 * text only at the UI layer.
 */
object StoryData {

    const val START_NODE = "intro_orphan"

    val creatures: Map<String, Creature> = listOf(
        Creature(
            id = "guard_shark",
            nameRes = R.string.creature_guard_shark_name,
            descriptionRes = R.string.creature_guard_shark_description,
            maxHealth = 42,
            attackPower = 12,
            victoryNodeId = "after_shark_victory",
            defeatNodeId = "death_shark",
            fleeNodeId = "after_shark_flee"
        ),
        Creature(
            id = "arogidigba",
            nameRes = R.string.creature_arogidigba_name,
            descriptionRes = R.string.creature_arogidigba_description,
            maxHealth = 60,
            attackPower = 15,
            victoryNodeId = "after_arogidigba_victory",
            defeatNodeId = "death_arogidigba",
            fleeNodeId = "after_arogidigba_flee"
        ),
        Creature(
            id = "flying_python",
            nameRes = R.string.creature_flying_python_name,
            descriptionRes = R.string.creature_flying_python_description,
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
            titleRes = R.string.node_intro_orphan_title,
            scene = SceneType.VILLAGE,
            textRes = R.string.node_intro_orphan_text,
            choices = listOf(
                Choice(
                    textRes = R.string.choice_intro_orphan_0,
                    nextNodeId = "call_to_sea",
                    effects = listOf(StatEffect(Stat.IGBOYA, 5))
                ),
                Choice(
                    textRes = R.string.choice_intro_orphan_1,
                    nextNodeId = "shrine_blessing"
                )
            )
        ),


        StoryNode(
            id = "shrine_blessing",
            titleRes = R.string.node_shrine_blessing_title,
            scene = SceneType.VILLAGE,
            textRes = R.string.node_shrine_blessing_text,
            grantsCharm = Charm.CALMING_SAND,
            choices = listOf(
                Choice(
                    textRes = R.string.choice_shrine_blessing_0,
                    nextNodeId = "call_to_sea",
                    effects = listOf(StatEffect(Stat.OOGUN, 5))
                )
            )
        ),


        StoryNode(
            id = "call_to_sea",
            titleRes = R.string.node_call_to_sea_title,
            scene = SceneType.RIVER,
            textRes = R.string.node_call_to_sea_text,
            choices = listOf(
                Choice(
                    textRes = R.string.choice_call_to_sea_0,
                    nextNodeId = "sea_path_direct",
                    effects = listOf(StatEffect(Stat.IGBOYA, 3))
                ),
                Choice(
                    textRes = R.string.choice_call_to_sea_1,
                    nextNodeId = "sea_path_careful",
                    effects = listOf(StatEffect(Stat.OOGUN, 3))
                )
            )
        ),


        StoryNode(
            id = "sea_path_direct",
            titleRes = R.string.node_sea_path_direct_title,
            scene = SceneType.RIVER,
            textRes = R.string.node_sea_path_direct_text,
            choices = listOf(
                Choice(textRes = R.string.choice_sea_path_direct_0, nextNodeId = "encounter_shark")
            )
        ),


        StoryNode(
            id = "sea_path_careful",
            titleRes = R.string.node_sea_path_careful_title,
            scene = SceneType.RIVER,
            textRes = R.string.node_sea_path_careful_text,
            choices = listOf(
                Choice(
                    textRes = R.string.choice_sea_path_careful_0,
                    nextNodeId = "encounter_shark"
                ),
                Choice(
                    textRes = R.string.choice_sea_path_careful_1,
                    nextNodeId = "meeting_ijinle",
                    effects = listOf(StatEffect(Stat.OOGUN, 2))
                )
            )
        ),


        // --- Guard-shark encounter ---
        StoryNode(
            id = "encounter_shark",
            titleRes = R.string.node_encounter_shark_title,
            scene = SceneType.RIVER,
            textRes = R.string.node_encounter_shark_text,
            encounterId = "guard_shark"
        ),


        StoryNode(
            id = "after_shark_victory",
            titleRes = R.string.node_after_shark_victory_title,
            scene = SceneType.RIVER,
            textRes = R.string.node_after_shark_victory_text,
            grantsCharm = Charm.BRASS_HORN,
            choices = listOf(
                Choice(textRes = R.string.choice_after_shark_victory_0, nextNodeId = "meeting_ijinle")
            )
        ),


        StoryNode(
            id = "after_shark_flee",
            titleRes = R.string.node_after_shark_flee_title,
            scene = SceneType.RIVER,
            textRes = R.string.node_after_shark_flee_text,
            choices = listOf(
                Choice(
                    textRes = R.string.choice_after_shark_flee_0,
                    nextNodeId = "meeting_ijinle",
                    effects = listOf(StatEffect(Stat.IGBOYA, -5))
                )
            )
        ),


        StoryNode(
            id = "death_shark",
            titleRes = R.string.node_death_shark_title,
            scene = SceneType.DEATH,
            textRes = R.string.node_death_shark_text,
            isEnding = true
        ),


        // --- Ijinle, the turtle spirit ---
        StoryNode(
            id = "meeting_ijinle",
            titleRes = R.string.node_meeting_ijinle_title,
            scene = SceneType.RIVER,
            textRes = R.string.node_meeting_ijinle_text,
            choices = listOf(
                Choice(
                    textRes = R.string.choice_meeting_ijinle_0,
                    nextNodeId = "ijinle_arogidigba_answer"
                ),
                Choice(
                    textRes = R.string.choice_meeting_ijinle_1,
                    nextNodeId = "ijinle_python_answer",
                    requirement = Requirement(Stat.OOGUN, 45)
                ),
                Choice(
                    textRes = R.string.choice_meeting_ijinle_2,
                    nextNodeId = "arogidigba_court"
                ),
                Choice(
                    textRes = R.string.choice_meeting_ijinle_3,
                    nextNodeId = "grotto_entrance"
                )
            )
        ),


        // --- Grotto side-quest (optional) ---
        StoryNode(
            id = "grotto_entrance",
            titleRes = R.string.node_grotto_entrance_title,
            scene = SceneType.CAVE,
            textRes = R.string.node_grotto_entrance_text,
            choices = listOf(
                Choice(
                    textRes = R.string.choice_grotto_entrance_0,
                    nextNodeId = "grotto_treasure",
                    requiresCharm = Charm.INNER_EYE
                ),
                Choice(
                    textRes = R.string.choice_grotto_entrance_1,
                    nextNodeId = "grotto_risk",
                    effects = listOf(StatEffect(Stat.IGBOYA, 5))
                ),
                Choice(
                    textRes = R.string.choice_grotto_entrance_2,
                    nextNodeId = "arogidigba_court"
                )
            )
        ),


        StoryNode(
            id = "grotto_treasure",
            titleRes = R.string.node_grotto_treasure_title,
            scene = SceneType.CAVE,
            textRes = R.string.node_grotto_treasure_text,
            grantsCharm = Charm.ADEORUN_TOKEN,
            choices = listOf(
                Choice(
                    textRes = R.string.choice_grotto_treasure_0,
                    nextNodeId = "arogidigba_court",
                    effects = listOf(StatEffect(Stat.OOGUN, 3))
                )
            )
        ),


        StoryNode(
            id = "grotto_risk",
            titleRes = R.string.node_grotto_risk_title,
            scene = SceneType.CAVE,
            textRes = R.string.node_grotto_risk_text,
            grantsCharm = Charm.ADEORUN_TOKEN,
            choices = listOf(
                Choice(
                    textRes = R.string.choice_grotto_risk_0,
                    nextNodeId = "arogidigba_court",
                    effects = listOf(StatEffect(Stat.ILERA, -15))
                )
            )
        ),


        StoryNode(
            id = "ijinle_arogidigba_answer",
            titleRes = R.string.node_ijinle_arogidigba_answer_title,
            scene = SceneType.RIVER,
            textRes = R.string.node_ijinle_arogidigba_answer_text,
            grantsCharm = Charm.INNER_EYE,
            choices = listOf(
                Choice(
                    textRes = R.string.choice_ijinle_arogidigba_answer_0,
                    nextNodeId = "arogidigba_court",
                    effects = listOf(StatEffect(Stat.OOGUN, 4))
                ),
                Choice(
                    textRes = R.string.choice_ijinle_arogidigba_answer_1,
                    nextNodeId = "grotto_entrance"
                )
            )
        ),


        StoryNode(
            id = "ijinle_python_answer",
            titleRes = R.string.node_ijinle_python_answer_title,
            scene = SceneType.RIVER,
            textRes = R.string.node_ijinle_python_answer_text,
            choices = listOf(
                Choice(
                    textRes = R.string.choice_ijinle_python_answer_0,
                    nextNodeId = "arogidigba_court",
                    setsFlag = "knows_python_weakness",
                    effects = listOf(StatEffect(Stat.OOGUN, 3))
                )
            )
        ),


        // ==================== ACT I — AROGIDIGBA'S COURT ====================
        StoryNode(
            id = "arogidigba_court",
            titleRes = R.string.node_arogidigba_court_title,
            scene = SceneType.SPIRIT_COURT,
            textRes = R.string.node_arogidigba_court_text,
            choices = listOf(
                Choice(
                    textRes = R.string.choice_arogidigba_court_0,
                    nextNodeId = "arogidigba_task",
                    effects = listOf(StatEffect(Stat.IGBOYA, 3))
                ),
                Choice(
                    textRes = R.string.choice_arogidigba_court_1,
                    nextNodeId = "arogidigba_seen_through",
                    requiresCharm = Charm.INNER_EYE
                )
            )
        ),


        StoryNode(
            id = "arogidigba_seen_through",
            titleRes = R.string.node_arogidigba_seen_through_title,
            scene = SceneType.SPIRIT_COURT,
            textRes = R.string.node_arogidigba_seen_through_text,
            choices = listOf(
                Choice(
                    textRes = R.string.choice_arogidigba_seen_through_0,
                    nextNodeId = "arogidigba_task",
                    setsFlag = "saw_through_arogidigba",
                    effects = listOf(StatEffect(Stat.OOGUN, 5))
                )
            )
        ),


        StoryNode(
            id = "arogidigba_task",
            titleRes = R.string.node_arogidigba_task_title,
            scene = SceneType.SPIRIT_COURT,
            textRes = R.string.node_arogidigba_task_text,
            choices = listOf(
                Choice(
                    textRes = R.string.choice_arogidigba_task_0,
                    nextNodeId = "mother_intervenes"
                )
            )
        ),


        StoryNode(
            id = "mother_intervenes",
            titleRes = R.string.node_mother_intervenes_title,
            scene = SceneType.SPIRIT_COURT,
            textRes = R.string.node_mother_intervenes_text,
            choices = listOf(
                Choice(textRes = R.string.choice_mother_intervenes_0, nextNodeId = "arogidigba_furious")
            )
        ),


        StoryNode(
            id = "arogidigba_furious",
            titleRes = R.string.node_arogidigba_furious_title,
            scene = SceneType.SPIRIT_COURT,
            textRes = R.string.node_arogidigba_furious_text,
            choices = listOf(
                Choice(
                    textRes = R.string.choice_arogidigba_furious_0,
                    nextNodeId = "peaceful_pass_sea",
                    requiresCharm = Charm.MOTHER_COWRIE,
                    requiresFlag = "saw_through_arogidigba"
                ),
                Choice(
                    textRes = R.string.choice_arogidigba_furious_1,
                    nextNodeId = "encounter_arogidigba"
                )
            )
        ),


        StoryNode(
            id = "peaceful_pass_sea",
            titleRes = R.string.node_peaceful_pass_sea_title,
            scene = SceneType.SPIRIT_COURT,
            textRes = R.string.node_peaceful_pass_sea_text,
            choices = listOf(
                Choice(
                    textRes = R.string.choice_peaceful_pass_sea_0,
                    nextNodeId = "washed_ashore",
                    setsFlag = "escaped_arogidigba_unharmed",
                    effects = listOf(StatEffect(Stat.OOGUN, 10))
                )
            )
        ),


        StoryNode(
            id = "encounter_arogidigba",
            titleRes = R.string.node_encounter_arogidigba_title,
            scene = SceneType.SPIRIT_COURT,
            textRes = R.string.node_encounter_arogidigba_text,
            encounterId = "arogidigba"
        ),


        StoryNode(
            id = "after_arogidigba_victory",
            titleRes = R.string.node_after_arogidigba_victory_title,
            scene = SceneType.SPIRIT_COURT,
            textRes = R.string.node_after_arogidigba_victory_text,
            grantsCharm = Charm.HEALING_LEAF,
            choices = listOf(
                Choice(textRes = R.string.choice_after_arogidigba_victory_0, nextNodeId = "washed_ashore")
            )
        ),


        StoryNode(
            id = "after_arogidigba_flee",
            titleRes = R.string.node_after_arogidigba_flee_title,
            scene = SceneType.SPIRIT_COURT,
            textRes = R.string.node_after_arogidigba_flee_text,
            choices = listOf(
                Choice(
                    textRes = R.string.choice_after_arogidigba_flee_0,
                    nextNodeId = "washed_ashore",
                    effects = listOf(StatEffect(Stat.IGBOYA, -5))
                )
            )
        ),


        StoryNode(
            id = "death_arogidigba",
            titleRes = R.string.node_death_arogidigba_title,
            scene = SceneType.DEATH,
            textRes = R.string.node_death_arogidigba_text,
            isEnding = true
        ),


        // ==================== ACT II — ÀLÙPÀYÌDÁ ====================
        StoryNode(
            id = "washed_ashore",
            titleRes = R.string.node_washed_ashore_title,
            scene = SceneType.VILLAGE,
            textRes = R.string.node_washed_ashore_text,
            choices = listOf(
                Choice(textRes = R.string.choice_washed_ashore_0, nextNodeId = "learn_of_python")
            )
        ),


        StoryNode(
            id = "learn_of_python",
            titleRes = R.string.node_learn_of_python_title,
            scene = SceneType.VILLAGE,
            textRes = R.string.node_learn_of_python_text,
            choices = listOf(
                Choice(
                    textRes = R.string.choice_learn_of_python_0,
                    nextNodeId = "shrine_gate",
                    effects = listOf(StatEffect(Stat.IGBOYA, 3))
                )
            )
        ),


        StoryNode(
            id = "shrine_gate",
            titleRes = R.string.node_shrine_gate_title,
            scene = SceneType.VILLAGE,
            textRes = R.string.node_shrine_gate_text,
            choices = listOf(
                Choice(
                    textRes = R.string.choice_shrine_gate_0,
                    nextNodeId = "python_approach",
                    effects = listOf(StatEffect(Stat.IGBOYA, 5))
                ),
                Choice(
                    textRes = R.string.choice_shrine_gate_1,
                    nextNodeId = "python_approach",
                    effects = listOf(StatEffect(Stat.OOGUN, 5))
                )
            )
        ),


        StoryNode(
            id = "python_approach",
            titleRes = R.string.node_python_approach_title,
            scene = SceneType.VILLAGE,
            textRes = R.string.node_python_approach_text,
            choices = listOf(
                Choice(
                    textRes = R.string.choice_python_approach_0,
                    nextNodeId = "python_weak_point",
                    requiresCharm = Charm.INNER_EYE
                ),
                Choice(
                    textRes = R.string.choice_python_approach_1,
                    nextNodeId = "encounter_python",
                    requiresFlag = "knows_python_weakness"
                ),
                Choice(
                    textRes = R.string.choice_python_approach_2,
                    nextNodeId = "encounter_python"
                )
            )
        ),


        StoryNode(
            id = "python_weak_point",
            titleRes = R.string.node_python_weak_point_title,
            scene = SceneType.VILLAGE,
            textRes = R.string.node_python_weak_point_text,
            choices = listOf(
                Choice(
                    textRes = R.string.choice_python_weak_point_0,
                    nextNodeId = "encounter_python",
                    setsFlag = "spotted_python_weakness"
                )
            )
        ),


        // --- Python encounter ---
        StoryNode(
            id = "encounter_python",
            titleRes = R.string.node_encounter_python_title,
            scene = SceneType.VILLAGE,
            textRes = R.string.node_encounter_python_text,
            encounterId = "flying_python"
        ),


        StoryNode(
            id = "after_python_victory",
            titleRes = R.string.node_after_python_victory_title,
            scene = SceneType.VICTORY,
            textRes = R.string.node_after_python_victory_text,
            choices = listOf(
                Choice(textRes = R.string.choice_after_python_victory_0, nextNodeId = "meeting_ifepade")
            )
        ),


        StoryNode(
            id = "after_python_flee",
            titleRes = R.string.node_after_python_flee_title,
            scene = SceneType.VILLAGE,
            textRes = R.string.node_after_python_flee_text,
            choices = listOf(
                Choice(
                    textRes = R.string.choice_after_python_flee_0,
                    nextNodeId = "encounter_python",
                    effects = listOf(StatEffect(Stat.IGBOYA, -5))
                )
            )
        ),


        StoryNode(
            id = "death_python",
            titleRes = R.string.node_death_python_title,
            scene = SceneType.DEATH,
            textRes = R.string.node_death_python_text,
            isEnding = true
        ),


        // --- Romance & the court's jealousy ---
        StoryNode(
            id = "meeting_ifepade",
            titleRes = R.string.node_meeting_ifepade_title,
            scene = SceneType.VILLAGE,
            textRes = R.string.node_meeting_ifepade_text,
            choices = listOf(
                Choice(textRes = R.string.choice_meeting_ifepade_0, nextNodeId = "palace_welcome")
            )
        ),


        StoryNode(
            id = "palace_welcome",
            titleRes = R.string.node_palace_welcome_title,
            scene = SceneType.SPIRIT_COURT,
            textRes = R.string.node_palace_welcome_text,
            choices = listOf(
                Choice(textRes = R.string.choice_palace_welcome_0, nextNodeId = "court_rumor")
            )
        ),


        StoryNode(
            id = "court_rumor",
            titleRes = R.string.node_court_rumor_title,
            scene = SceneType.SPIRIT_COURT,
            textRes = R.string.node_court_rumor_text,
            choices = listOf(
                Choice(
                    textRes = R.string.choice_court_rumor_0,
                    nextNodeId = "confront_accuser",
                    requirement = Requirement(Stat.IGBOYA, 55)
                ),
                Choice(
                    textRes = R.string.choice_court_rumor_1,
                    nextNodeId = "flee_alupayida"
                )
            )
        ),


        StoryNode(
            id = "confront_accuser",
            titleRes = R.string.node_confront_accuser_title,
            scene = SceneType.SPIRIT_COURT,
            textRes = R.string.node_confront_accuser_text,
            choices = listOf(
                Choice(
                    textRes = R.string.choice_confront_accuser_0,
                    nextNodeId = "reconciliation",
                    effects = listOf(StatEffect(Stat.ILERA, -20))
                ),
                Choice(
                    textRes = R.string.choice_confront_accuser_1,
                    nextNodeId = "flee_alupayida"
                )
            )
        ),


        StoryNode(
            id = "flee_alupayida",
            titleRes = R.string.node_flee_alupayida_title,
            scene = SceneType.VILLAGE,
            textRes = R.string.node_flee_alupayida_text,
            choices = listOf(
                Choice(textRes = R.string.choice_flee_alupayida_0, nextNodeId = "ifepade_search")
            )
        ),


        StoryNode(
            id = "ifepade_search",
            titleRes = R.string.node_ifepade_search_title,
            scene = SceneType.VILLAGE,
            textRes = R.string.node_ifepade_search_text,
            choices = listOf(
                Choice(textRes = R.string.choice_ifepade_search_0, nextNodeId = "reconciliation")
            )
        ),


        StoryNode(
            id = "reconciliation",
            titleRes = R.string.node_reconciliation_title,
            scene = SceneType.SPIRIT_COURT,
            textRes = R.string.node_reconciliation_text,
            choices = listOf(
                Choice(textRes = R.string.choice_reconciliation_0, nextNodeId = "king_sends_quest")
            )
        ),


        StoryNode(
            id = "king_sends_quest",
            titleRes = R.string.node_king_sends_quest_title,
            scene = SceneType.SPIRIT_COURT,
            textRes = R.string.node_king_sends_quest_text,
            choices = listOf(
                Choice(
                    textRes = R.string.choice_king_sends_quest_0,
                    nextNodeId = "mountain_path",
                    effects = listOf(StatEffect(Stat.IGBOYA, 5))
                )
            )
        ),


        // ==================== ACT III — THE MOUNTAIN OF TRIALS ====================
        StoryNode(
            id = "mountain_path",
            titleRes = R.string.node_mountain_path_title,
            scene = SceneType.FOREST_PATH,
            textRes = R.string.node_mountain_path_text,
            choices = listOf(
                Choice(
                    textRes = R.string.choice_mountain_path_0,
                    nextNodeId = "ijapa_riddle"
                ),
                Choice(
                    textRes = R.string.choice_mountain_path_1,
                    nextNodeId = "town_of_men"
                )
            )
        ),


        StoryNode(
            id = "ijapa_riddle",
            titleRes = R.string.node_ijapa_riddle_title,
            scene = SceneType.FOREST_PATH,
            textRes = R.string.node_ijapa_riddle_text,
            choices = listOf(
                Choice(textRes = R.string.choice_ijapa_riddle_0, nextNodeId = "ijapa_success"),
                Choice(textRes = R.string.choice_ijapa_riddle_1, nextNodeId = "ijapa_fail"),
                Choice(textRes = R.string.choice_ijapa_riddle_2, nextNodeId = "town_of_men")
            )
        ),


        StoryNode(
            id = "ijapa_success",
            titleRes = R.string.node_ijapa_success_title,
            scene = SceneType.FOREST_PATH,
            textRes = R.string.node_ijapa_success_text,
            choices = listOf(
                Choice(
                    textRes = R.string.choice_ijapa_success_0,
                    nextNodeId = "itanforiti_threshold",
                    setsFlag = "used_ijapa_shortcut",
                    effects = listOf(StatEffect(Stat.OOGUN, 5))
                )
            )
        ),


        StoryNode(
            id = "ijapa_fail",
            titleRes = R.string.node_ijapa_fail_title,
            scene = SceneType.FOREST_PATH,
            textRes = R.string.node_ijapa_fail_text,
            choices = listOf(
                Choice(
                    textRes = R.string.choice_ijapa_fail_0,
                    nextNodeId = "town_of_men",
                    effects = listOf(StatEffect(Stat.IGBOYA, -3))
                )
            )
        ),


        StoryNode(
            id = "town_of_men",
            titleRes = R.string.node_town_of_men_title,
            scene = SceneType.VILLAGE,
            textRes = R.string.node_town_of_men_text,
            choices = listOf(
                Choice(textRes = R.string.choice_town_of_men_0, nextNodeId = "town_of_women")
            )
        ),


        StoryNode(
            id = "town_of_women",
            titleRes = R.string.node_town_of_women_title,
            scene = SceneType.VILLAGE,
            textRes = R.string.node_town_of_women_text,
            choices = listOf(
                Choice(textRes = R.string.choice_town_of_women_0, nextNodeId = "itanforiti_threshold")
            )
        ),


        StoryNode(
            id = "itanforiti_threshold",
            titleRes = R.string.node_itanforiti_threshold_title,
            scene = SceneType.CAVE,
            textRes = R.string.node_itanforiti_threshold_text,
            choices = listOf(
                Choice(
                    textRes = R.string.choice_itanforiti_threshold_0,
                    nextNodeId = "itanforiti_meeting"
                ),
                Choice(
                    textRes = R.string.choice_itanforiti_threshold_1,
                    nextNodeId = "itanforiti_token",
                    requiresCharm = Charm.ADEORUN_TOKEN
                )
            )
        ),


        StoryNode(
            id = "itanforiti_token",
            titleRes = R.string.node_itanforiti_token_title,
            scene = SceneType.CAVE,
            textRes = R.string.node_itanforiti_token_text,
            choices = listOf(
                Choice(
                    textRes = R.string.choice_itanforiti_token_0,
                    nextNodeId = "itanforiti_meeting",
                    setsFlag = "vouched_by_adeorun",
                    effects = listOf(StatEffect(Stat.OOGUN, 8))
                )
            )
        ),


        StoryNode(
            id = "itanforiti_meeting",
            titleRes = R.string.node_itanforiti_meeting_title,
            scene = SceneType.CAVE,
            textRes = R.string.node_itanforiti_meeting_text,
            choices = listOf(
                Choice(
                    textRes = R.string.choice_itanforiti_meeting_0,
                    nextNodeId = "itanforiti_pride_lesson",
                    requirement = Requirement(Stat.OOGUN, 55)
                ),
                Choice(
                    textRes = R.string.choice_itanforiti_meeting_1,
                    nextNodeId = "itanforiti_love_lesson"
                ),
                Choice(
                    textRes = R.string.choice_itanforiti_meeting_2,
                    nextNodeId = "itanforiti_honesty_lesson"
                )
            )
        ),


        StoryNode(
            id = "itanforiti_pride_lesson",
            titleRes = R.string.node_itanforiti_pride_lesson_title,
            scene = SceneType.CAVE,
            textRes = R.string.node_itanforiti_pride_lesson_text,
            choices = listOf(
                Choice(textRes = R.string.choice_itanforiti_pride_lesson_0, nextNodeId = "return_to_alupayida")
            )
        ),


        StoryNode(
            id = "itanforiti_love_lesson",
            titleRes = R.string.node_itanforiti_love_lesson_title,
            scene = SceneType.CAVE,
            textRes = R.string.node_itanforiti_love_lesson_text,
            choices = listOf(
                Choice(
                    textRes = R.string.choice_itanforiti_love_lesson_0,
                    nextNodeId = "return_to_alupayida",
                    setsFlag = "learned_of_love"
                )
            )
        ),


        StoryNode(
            id = "itanforiti_honesty_lesson",
            titleRes = R.string.node_itanforiti_honesty_lesson_title,
            scene = SceneType.CAVE,
            textRes = R.string.node_itanforiti_honesty_lesson_text,
            choices = listOf(
                Choice(
                    textRes = R.string.choice_itanforiti_honesty_lesson_0,
                    nextNodeId = "return_to_alupayida",
                    setsFlag = "learned_of_honesty"
                )
            )
        ),


        // ==================== ENDING ====================
        StoryNode(
            id = "return_to_alupayida",
            titleRes = R.string.node_return_to_alupayida_title,
            scene = SceneType.SPIRIT_COURT,
            textRes = R.string.node_return_to_alupayida_text,
            choices = listOf(
                Choice(
                    textRes = R.string.choice_return_to_alupayida_0,
                    nextNodeId = "ending_love",
                    requiresFlag = "learned_of_love"
                ),
                Choice(
                    textRes = R.string.choice_return_to_alupayida_1,
                    nextNodeId = "ending_pride_reformed",
                    requiresFlag = "learned_of_honesty"
                ),
                Choice(
                    textRes = R.string.choice_return_to_alupayida_2,
                    nextNodeId = "ending_dutiful"
                ),
                Choice(
                    textRes = R.string.choice_return_to_alupayida_3,
                    nextNodeId = "ending_secret",
                    requiresCharm = Charm.ADEORUN_TOKEN,
                    requiresFlag = "vouched_by_adeorun"
                )
            )
        ),


        StoryNode(
            id = "ending_love",
            titleRes = R.string.node_ending_love_title,
            scene = SceneType.VICTORY,
            textRes = R.string.node_ending_love_text,
            isEnding = true
        ),


        StoryNode(
            id = "ending_pride_reformed",
            titleRes = R.string.node_ending_pride_reformed_title,
            scene = SceneType.VICTORY,
            textRes = R.string.node_ending_pride_reformed_text,
            isEnding = true
        ),


        StoryNode(
            id = "ending_dutiful",
            titleRes = R.string.node_ending_dutiful_title,
            scene = SceneType.VICTORY,
            textRes = R.string.node_ending_dutiful_text,
            isEnding = true
        ),


        StoryNode(
            id = "ending_secret",
            titleRes = R.string.node_ending_secret_title,
            scene = SceneType.VICTORY,
            textRes = R.string.node_ending_secret_text,
            isEnding = true
        )


    ).associateBy { it.id }
}
