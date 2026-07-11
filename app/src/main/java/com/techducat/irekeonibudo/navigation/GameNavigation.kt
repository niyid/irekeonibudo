package com.techducat.irekeonibudo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.techducat.irekeonibudo.data.Screen
import com.techducat.irekeonibudo.ui.screens.EncounterScreen
import com.techducat.irekeonibudo.ui.screens.EndingScreen
import com.techducat.irekeonibudo.ui.screens.InventoryScreen
import com.techducat.irekeonibudo.ui.screens.StoryScreen
import com.techducat.irekeonibudo.ui.screens.TitleScreen
import com.techducat.irekeonibudo.viewmodel.GameViewModel

@Composable
fun IrekeOnibudoApp(viewModel: GameViewModel) {
    val state by viewModel.state.collectAsState()

    when (state.screen) {
        Screen.TITLE -> TitleScreen(
            hasSave = { viewModel.hasSaveSuspend() },
            onNewGame = { viewModel.startNewGame() },
            onContinue = { viewModel.continueGame() }
        )

        Screen.STORY -> StoryScreen(
            state = state,
            onChoice = { choice -> viewModel.choose(choice) },
            onOpenInventory = { viewModel.toggleInventory(true) }
        )

        Screen.ENCOUNTER -> state.activeEncounter?.let { encounter ->
            EncounterScreen(
                state = state,
                encounter = encounter,
                onAttack = { viewModel.encounterAttack() },
                onUseCharm = { charm -> viewModel.encounterUseCharm(charm) },
                onFlee = { viewModel.encounterFlee() }
            )
        }

        Screen.INVENTORY -> InventoryScreen(
            player = state.player,
            onBack = { viewModel.toggleInventory(false) }
        )

        Screen.ENDING -> EndingScreen(
            node = state.currentNode,
            onPlayAgain = { viewModel.startNewGame() }
        )
    }
}
