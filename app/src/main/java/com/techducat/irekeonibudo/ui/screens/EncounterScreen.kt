package com.techducat.irekeonibudo.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.techducat.irekeonibudo.data.Charm
import com.techducat.irekeonibudo.data.EncounterState
import com.techducat.irekeonibudo.data.GameState
import com.techducat.irekeonibudo.ui.components.PlayerStatusBar
import com.techducat.irekeonibudo.ui.components.SceneCanvas
import com.techducat.irekeonibudo.ui.components.StatBar
import com.techducat.irekeonibudo.ui.theme.BloodRed
import com.techducat.irekeonibudo.viewmodel.GameViewModel

@Composable
fun EncounterScreen(
    state: GameState,
    encounter: EncounterState,
    onAttack: () -> Unit,
    onUseCharm: (Charm) -> Unit,
    onFlee: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        PlayerStatusBar(player = state.player)
        Column(modifier = Modifier
            .weight(1f)
            .padding(horizontal = 20.dp)) {

            SceneCanvas(scene = state.currentNode.scene)

            Text(
                text = encounter.creature.name,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = 12.dp)
            )
            Text(
                text = encounter.creature.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )
            StatBar(
                label = encounter.creature.name,
                value = encounter.creatureHealth,
                max = encounter.creature.maxHealth,
                color = BloodRed,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(encounter.roundLog.takeLast(6)) { line ->
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }

            if (encounter.playerTurn) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Button(onClick = onAttack, modifier = Modifier.weight(1f)) {
                        Text("Attack")
                    }
                    OutlinedButton(onClick = onFlee, modifier = Modifier.weight(1f)) {
                        Text("Flee")
                    }
                }
                if (state.player.charms.any { it != Charm.ADEORUN_TOKEN }) {
                    Text(
                        text = "Charms",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    state.player.charms.filter { it != Charm.ADEORUN_TOKEN }.forEach { charm ->
                        val costsOogun = charm != Charm.HEALING_LEAF
                        val affordable = !costsOogun || state.player.oogun >= GameViewModel.CHARM_OOGUN_COST
                        OutlinedButton(
                            onClick = { onUseCharm(charm) },
                            enabled = affordable,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            Text(
                                if (costsOogun) {
                                    "${charm.displayName}  (${GameViewModel.CHARM_OOGUN_COST} oògùn)"
                                } else {
                                    charm.displayName
                                }
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = "…",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 20.dp)
                )
            }
        }
    }
}
