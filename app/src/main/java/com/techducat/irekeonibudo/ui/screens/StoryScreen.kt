package com.techducat.irekeonibudo.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.techducat.irekeonibudo.R
import com.techducat.irekeonibudo.data.Choice
import com.techducat.irekeonibudo.data.GameState
import com.techducat.irekeonibudo.ui.components.AppBackground
import com.techducat.irekeonibudo.ui.components.ChoiceButton
import com.techducat.irekeonibudo.ui.components.IllustrationFrame
import com.techducat.irekeonibudo.ui.components.PlayerStatusBar
import com.techducat.irekeonibudo.ui.components.SceneCanvas
import com.techducat.irekeonibudo.ui.theme.EmberGold

@Composable
fun StoryScreen(
    state: GameState,
    onChoice: (Choice) -> Unit,
    onOpenInventory: () -> Unit
) {
    AppBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            PlayerStatusBar(player = state.player)
            AnimatedContent(
                targetState = state.currentNode,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(360))) togetherWith
                        (fadeOut(animationSpec = tween(180)))
                },
                modifier = Modifier.weight(1f),
                label = "storyNodeTransition"
            ) { node ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    IllustrationFrame(modifier = Modifier.padding(top = 12.dp)) {
                        SceneCanvas(scene = node.scene)
                    }
                    Text(
                        text = stringResource(node.titleRes),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            shadow = Shadow(color = EmberGold.copy(alpha = 0.25f), offset = Offset(0f, 0f), blurRadius = 16f)
                        ),
                        modifier = Modifier.padding(top = 18.dp, bottom = 8.dp)
                    )
                    Text(
                        text = stringResource(node.textRes),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    state.availableChoices(node).forEach { choice ->
                        ChoiceButton(text = stringResource(choice.textRes), onClick = { onChoice(choice) })
                    }
                    ChoiceButton(text = stringResource(R.string.story_choice_inventory), onClick = onOpenInventory)
                }
            }
        }
    }
}
