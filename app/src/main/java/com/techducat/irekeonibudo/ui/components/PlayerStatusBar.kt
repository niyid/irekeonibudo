package com.techducat.irekeonibudo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.techducat.irekeonibudo.data.Player
import com.techducat.irekeonibudo.ui.theme.BloodRed
import com.techducat.irekeonibudo.ui.theme.DeepSeaBlue
import com.techducat.irekeonibudo.ui.theme.EmberGold
import com.techducat.irekeonibudo.ui.theme.MidSeaTeal
import com.techducat.irekeonibudo.ui.theme.SpiritViolet

@Composable
fun PlayerStatusBar(player: Player, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(MidSeaTeal.copy(alpha = 0.55f), DeepSeaBlue.copy(alpha = 0.85f))
                )
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            StatBar(label = "Ìgboyà", value = player.igboya, color = EmberGold, modifier = Modifier.weight(1f))
            StatBar(label = "Oògùn", value = player.oogun, color = SpiritViolet, modifier = Modifier.weight(1f))
            StatBar(label = "Ìlera", value = player.ilera, color = BloodRed, modifier = Modifier.weight(1f))
        }
        // A hairline gold seam along the bottom edge, separating chrome from the scene below.
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(EmberGold.copy(alpha = 0f), EmberGold.copy(alpha = 0.45f), EmberGold.copy(alpha = 0f))
                    )
                )
        )
    }
}
