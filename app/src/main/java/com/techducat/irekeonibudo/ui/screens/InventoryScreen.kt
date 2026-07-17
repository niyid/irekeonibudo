package com.techducat.irekeonibudo.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.techducat.irekeonibudo.data.Player
import com.techducat.irekeonibudo.ui.components.AppBackground
import com.techducat.irekeonibudo.ui.components.PlayerStatusBar
import com.techducat.irekeonibudo.ui.theme.EmberGold
import com.techducat.irekeonibudo.ui.theme.MidSeaTeal

@Composable
fun InventoryScreen(player: Player, onBack: () -> Unit) {
    AppBackground {
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
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(player.charms) { charm ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(MidSeaTeal.copy(alpha = 0.9f), MidSeaTeal.copy(alpha = 0.55f))
                                )
                            )
                            .border(
                                width = 1.dp,
                                brush = Brush.verticalGradient(
                                    listOf(EmberGold.copy(alpha = 0.65f), EmberGold.copy(alpha = 0.15f))
                                ),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .padding(16.dp)
                    ) {
                        // A small glowing charm token stands in for an icon/asset.
                        CharmToken()
                        Column {
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
}

@Composable
private fun CharmToken() {
    Canvas(modifier = Modifier.size(28.dp)) {
        val r = size.minDimension / 2f
        drawCircle(
            brush = Brush.radialGradient(
                listOf(EmberGold.copy(alpha = 0.9f), EmberGold.copy(alpha = 0f))
            ),
            radius = r * 1.6f
        )
        drawCircle(color = EmberGold.copy(alpha = 0.85f), radius = r * 0.55f)
        drawCircle(color = EmberGold.copy(alpha = 0.35f), radius = r * 0.9f, style = Stroke(width = 1.5f))
    }
}
