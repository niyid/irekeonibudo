package com.techducat.irekeonibudo.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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

    AnimatedContent(
        targetState = state.screen,
        transitionSpec = {
            (fadeIn(animationSpec = tween(320))) togetherWith (fadeOut(animationSpec = tween(160)))
        },
        label = "screenTransition"
    ) { screen ->
        when (screen) {
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

            Screen.ENCOUNTER -> state.activeEncounter?.let { duel ->
                EncounterScreen(
                    state = state,
                    duel = duel,
                    onTick = { deltaMs -> viewModel.duelTick(deltaMs) },
                    onDodge = { direction -> viewModel.duelDodge(direction) },
                    onBlockStart = { viewModel.duelBlockStart() },
                    onBlockEnd = { viewModel.duelBlockEnd() },
                    onAttack = { viewModel.duelAttack() },
                    onUseCharm = { charm -> viewModel.duelUseCharm(charm) },
                    onFlee = { viewModel.duelFlee() }
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
}
