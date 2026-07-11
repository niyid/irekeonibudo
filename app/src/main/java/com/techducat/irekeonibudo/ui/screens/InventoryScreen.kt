package com.techducat.irekeonibudo.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.techducat.irekeonibudo.data.Player
import com.techducat.irekeonibudo.ui.components.PlayerStatusBar
import com.techducat.irekeonibudo.ui.theme.ForestMidGreen

@Composable
fun InventoryScreen(player: Player, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        PlayerStatusBar(player = player)
        Text(
            text = "Charms Gathered",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        )
        if (player.charms.isEmpty()) {
            Text(
                text = "You carry nothing but the clothes on your back.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(player.charms) { charm ->
                    Card(colors = CardDefaults.cardColors(containerColor = ForestMidGreen)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = charm.displayName, style = MaterialTheme.typography.titleLarge)
                            Text(text = charm.description, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text("Back")
        }
    }
}
