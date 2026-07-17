package com.techducat.irekeonibudo.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.techducat.irekeonibudo.data.StoryNode
import com.techducat.irekeonibudo.ui.components.AppBackground
import com.techducat.irekeonibudo.ui.components.IllustrationFrame
import com.techducat.irekeonibudo.ui.components.SceneCanvas
import com.techducat.irekeonibudo.ui.theme.EmberGold

@Composable
fun EndingScreen(node: StoryNode, onPlayAgain: () -> Unit) {
    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IllustrationFrame {
                SceneCanvas(scene = node.scene)
            }
            Text(
                text = node.title,
                style = MaterialTheme.typography.headlineLarge.copy(
                    shadow = Shadow(color = EmberGold.copy(alpha = 0.4f), offset = Offset(0f, 0f), blurRadius = 22f)
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 22.dp, bottom = 4.dp)
            )
            // A small ornamental divider — a nod to the crown/charms motif rather than a stock rule line.
            Spacer(
                Modifier
                    .padding(vertical = 12.dp)
                    .width(64.dp)
                    .height(2.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(EmberGold.copy(alpha = 0f), EmberGold, EmberGold.copy(alpha = 0f))
                        )
                    )
            )
            Text(
                text = node.text,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            Button(onClick = onPlayAgain, modifier = Modifier.fillMaxWidth(0.8f)) {
                Text("Begin a New Journey")
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
