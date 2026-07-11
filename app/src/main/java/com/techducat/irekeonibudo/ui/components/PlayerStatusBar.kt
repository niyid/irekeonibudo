package com.techducat.irekeonibudo.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.techducat.irekeonibudo.data.Player
import com.techducat.irekeonibudo.ui.theme.BloodRed
import com.techducat.irekeonibudo.ui.theme.EmberGold
import com.techducat.irekeonibudo.ui.theme.SpiritViolet

@Composable
fun PlayerStatusBar(player: Player, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        StatBar(label = "Ìgboyà", value = player.igboya, color = EmberGold, modifier = Modifier.weight(1f))
        StatBar(label = "Oògùn", value = player.oogun, color = SpiritViolet, modifier = Modifier.weight(1f))
        StatBar(label = "Ìlera", value = player.ilera, color = BloodRed, modifier = Modifier.weight(1f))
    }
}
