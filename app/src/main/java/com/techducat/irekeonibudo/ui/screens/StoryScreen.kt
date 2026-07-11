package com.techducat.irekeonibudo.ui.screens

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
import androidx.compose.ui.unit.dp
import com.techducat.irekeonibudo.data.Choice
import com.techducat.irekeonibudo.data.GameState
import com.techducat.irekeonibudo.ui.components.ChoiceButton
import com.techducat.irekeonibudo.ui.components.PlayerStatusBar
import com.techducat.irekeonibudo.ui.components.SceneCanvas

@Composable
fun StoryScreen(
    state: GameState,
    onChoice: (Choice) -> Unit,
    onOpenInventory: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        PlayerStatusBar(player = state.player)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Top
        ) {
            SceneCanvas(scene = state.currentNode.scene)
            Text(
                text = state.currentNode.title,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            Text(
                text = state.currentNode.text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            state.availableChoices().forEach { choice ->
                ChoiceButton(text = choice.text, onClick = { onChoice(choice) })
            }
            ChoiceButton(text = "🎒  Inventory", onClick = onOpenInventory)
        }
    }
}
